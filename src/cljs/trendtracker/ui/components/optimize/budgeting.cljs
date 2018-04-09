(ns trendtracker.ui.components.optimize.budgeting
  (:require [keechma.ui-component :as ui]
            [trendtracker.utils :as u]
            [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [route> sub> <cmd]]
            [reagent.core :as r]
            [reacharts.recharts :as recharts]))

(def columns
  [{:title "지표" :dataIndex :kpi}
   {:title "지난 30일" :dataIndex :as-is :className "numbers"
    :render #(r/as-element (u/int-fmt %))}
   {:title "예상 성과 (30일)" :dataIndex :expected :className "numbers"
    :render #(r/as-element (u/int-fmt %))}
   {:title "변화" :dataIndex :delta :className "numbers"
    :render #(r/as-element
              [:div
               (u/int-fmt %)
               [ant/icon {:type (if (pos? %)
                                  :arrow-up
                                  :arrow-down)
                          :style {:color (if (pos? %)
                                           :green
                                           :red)}}]])}
   {:title "변화%" :dataIndex :pct-delta :className "numbers"
    :render #(r/as-element
              [:div
               (u/pct-fmt %)
               [ant/icon {:type (if (pos? %)
                                  :arrow-up
                                  :arrow-down)
                          :style {:color (if (pos? %)
                                           :green
                                           :red)}}]])}])

(defn nav-buttons [ctx]
  (let [route (route> ctx)
        client (:client route)
        modal-visible? (boolean (:modal route))]
    [:div
     [ant/modal {:visible modal-visible?
                 :title "입찰가 확인"
                 :onOk #(ui/redirect ctx (dissoc route :modal :guarantee-min))
                 :onCancel #(ui/redirect ctx (dissoc route :modal :guarantee-min))
                 :width "80%"
                 :footer nil}
      [(ui/component ctx :detail)]]
     [ant/form-item {:style {:margin-bottom 0 :margin-top 16}}
      [ant/button-group
       [ant/button
        {:icon "close-circle-o"
         :on-click #(do (ui/redirect ctx {:client client :page "optimize"})
                        (js/setTimeout
                         (fn []
                           (ant/notification-warning
                            {:message "최적화 설정을 취소했습니다"
                             :placement "bottomRight"}))
                         100))}
        "취소"]
       [ant/button
        {:icon "bars"
         :on-click #(ui/redirect ctx (assoc route :modal true))}
        "키워드별 입찰가 확인"]
       [ant/button
        {:icon "save"
         :type "primary"
         :on-click #(do
                      (<cmd ctx :save)
                      (ant/notification-success
                       {:message "최적화 설정이 반영되었습니다"
                        :placement "bottomRight"}))}
        "설정 저장"]]]]))

(defn totals
  [budget stats]
  [:ul.totals
   [:li.total {:style {:border-right "1px solid #e6ebf1"}}
    [:h2 (u/krw budget)]
    [:p "예산"]]
   [:li.total {:style {:border-right "1px solid #e6ebf1"}}
    [:h2 (u/int-fmt (Math/floor (:clicks stats)))]
    [:p {:style {:color "#1890ff"}} "예상 클릭수"]]
   [:li.total {:style {:border-right "1px solid #e6ebf1"}}
    [:h2 (u/pct-fmt (:ctr stats))]
    [:p {:style {:color "#9254de"}} "예상 클릭률 (CTR)"]]
   [:li.total
    [:h2 (u/krw (Math/ceil (:cpc stats)))]
    [:p {:style {:color "#7cb305"}} "예상 CPC"]]])

(defn slider-detail [budget budget-view]
  [recharts/responsive-container {:height 200}
   [recharts/composed-chart {:data budget-view}
    [recharts/y-axis {:yAxisId "left"
                      :tickFormatter u/pct-fmt
                      :label {:value "CTR" :angle 270 :position "insideLeft"}}]
    [recharts/y-axis {:yAxisId "right" :orientation "right"
                      :tickFormatter u/krw
                      :label {:angle 90 :value "CPC" :position "insideRight"}}]
    [recharts/x-axis
     {:dataKey :cost :type :number :tick false :domain [0 "dataMax"]}]
    [recharts/tooltip {:formatter #(if (> 1 %)
                                     (u/pct-fmt %)
                                     (u/krw %))
                       :labelFormatter u/krw}]
    [recharts/reference-line {:x budget
                              :label "예산"
                              :stroke "#ff7875"}]
    [recharts/line {:dataKey :cpc
                    :yAxisId "right"
                    :type "monotone"
                    :dot false
                    :stroke "#a0d911"}]
    [recharts/line {:dataKey :ctr
                    :yAxisId "left"
                    :type "monotone"
                    :dot false
                    :stroke "#b37feb"}]]])

(defn step-2 [ctx]
  (let [conversion-funnel (ui/component ctx :conversion-funnel)
        slider (ui/component ctx :slider)
        settings (sub> ctx :optimize-settings)
        budget (:budget settings)
        stats (sub> ctx :optimize-stats)
        ridgeline (sub> ctx :ridgeline)
        budget-view (take-nth (->> ridgeline count Math/sqrt Math/ceil) ridgeline)]
    [ant/spin {:spinning (not stats)}
     [ant/card
      [ant/row
       [ant/col
        [totals budget (sub> ctx :optimize-stats)]]]
      [ant/row {:type "flex" :justify "space-around"}
       [ant/col {:span 17}
        [slider]]]
      [ant/row {:type "flex" :justify "space-around"}
       [ant/col {:span 19}
        [slider-detail budget budget-view]]]
      
      ;; (when (pos? (:impressions stats))
      ;;   [ant/row {:type "flex" :justify "space-around"}
      ;;    [ant/col {:span 20} [conversion-funnel]]])

      [ant/row
       [ant/col
        [ant/table
         {:dataSource
          (map
           #(assoc %
                   :delta (- (:expected %) (:as-is %))
                   :pct-delta (u/delta (:as-is %) (:expected %)))
           [{:kpi "예상 비용" :as-is 1100100 :expected (:cost stats)}
            {:kpi "노출수" :as-is 1620589 :expected (:impressions stats)}
            {:kpi "클릭수" :as-is 1064 :expected (:clicks stats)}
            {:kpi "CPC" :as-is 1033 :expected (Math/ceil (:cpc stats))}])
            ;; {:kpi "클릭률" :as-is 0.0007 :expected (/ (:clicks stats) (:impressions stats))}

          :bordered true
          :pagination false
          :columns columns
          :size "middle"
          :rowKey :kpi}]]]
      [nav-buttons ctx]]]))

(def component
  (ui/constructor
   {:renderer step-2
    :topic :optimize
    :component-deps [:conversion-funnel
                     :slider
                     :detail]
    :subscription-deps [:optimize-settings
                        :optimize-stats
                        :ridgeline]}))
