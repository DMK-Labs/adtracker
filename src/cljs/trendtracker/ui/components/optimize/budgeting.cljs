(ns trendtracker.ui.components.optimize.budgeting
  (:require [keechma.ui-component :as ui]
            [trendtracker.utils :as u]
            [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [route> sub> <cmd]]
            [reagent.core :as r]))

(def columns
  [{:title "지표" :dataIndex :kpi}
   ;; {:title "지난 30일" :dataIndex :as-is :className "numbers"
   ;;  :render #(r/as-element (u/int-fmt %))}
   {:title "예상 성과 (30일)" :dataIndex :to-be :className "numbers"
    :render #(r/as-element (u/int-fmt %))}
   ;; {:title "변화" :dataIndex :delta :className "numbers"
   ;;  :render #(r/as-element
   ;;            [:div
   ;;             (u/int-fmt %)
   ;;             [ant/icon {:type :arrow-up :style {:color :green}}]])}
   ;; {:title "% 변화" :dataIndex :pct-delta
   ;;  :render #(r/as-element
   ;;            [:div
   ;;             (u/pct-fmt 2 %)
   ;;             [ant/icon {:type :arrow-up :style {:color :green}}]])}
   ])

(defn gen [n]
  [{:name "노출" :as-is 10000 :to-be (/ (inc n) 100)}
   {:name "클릭" :as-is 2000 :to-be (/ (inc n) 1000)}
   {:name "전환" :as-is 100 :to-be (/ (inc n) 10000)}])

(defn nav-buttons [ctx]
  (let [client (:client (route> ctx))]
    [ant/form-item {:style {:margin-bottom 0
                            :margin-top 16}}
     [ant/button-group
      [ant/button
       {:icon "left"
        :on-click #(ui/redirect ctx {:client client
                                     :page "optimize"
                                     :subpage "new"
                                     :step 1})}
       "이전"]
      [ant/button
       {:type "primary"
        :on-click #(ui/redirect ctx {:client client
                                     :page "optimize"
                                     :subpage "new"
                                     :step 3})}
       "세부 지표 확인" [ant/icon {:type "right"}]]]]))

(defn totals [budget stats]
  [:ul.totals
   [:li.total {:style {:border-right "1px solid #e6ebf1"}}
    [:h2 (u/krw budget)]
    [:p "예상 예산"]]
   [:li.total {:style {:border-right "1px solid #e6ebf1"}}
    [:h2 (u/int-fmt (Math/floor (:clicks stats)))]
    [:p "예상 클릭수"]]
   [:li.total
    [:h2 (u/krw (Math/ceil (:cpc stats)))]
    [:p "예상 CPC"]]])

(def data
  (mapv #(assoc % :delta (- (:to-be %) (:as-is %))
                :pct-delta (u/delta (:as-is %) (:to-be %)))
        [{:kpi "노출수" :as-is 66219 :to-be 87109}
         {:kpi "클릭수" :as-is 3910 :to-be 4333}
         {:kpi "전환수" :as-is 910 :to-be 1120}
         {:kpi "CPC" :as-is 320 :to-be 129}
         {:kpi "클릭률" :as-is 66219 :to-be 87109}
         {:kpi "전환률" :as-is 3910 :to-be 4333}
         {:kpi "ROAS" :as-is 320 :to-be 129}]))

(defn step-2 [ctx]
  (let [conversion-funnel (ui/component ctx :conversion-funnel)
        slider (ui/component ctx :slider)
        {:keys [budget] :as settings} (sub> ctx :optimize-settings)
        stats (sub> ctx :optimize-stats)]
    [ant/card
     [ant/row [ant/col [totals budget (sub> ctx :optimize-stats)]]]

     [ant/row {:type "flex" :justify "space-around"}
      [ant/col {:span 18}
       [slider]]]

     [ant/row {:type :flex :justify :space-around}
      [conversion-funnel
       [{:name "노출" :to-be (or (:impressions stats) 0)}
        {:name "클릭" :to-be (or (:clicks stats) 0)}
        {:name "전환" :to-be (or (:conversions stats) 0)}]]]

     [ant/row
      [ant/col
       [ant/table
        {:dataSource (mapv #(assoc % :delta (- (:to-be %) (:as-is %))
                                   :pct-delta (u/delta (:as-is %) (:to-be %)))
                           [{:kpi   "예상 비용"
                             :to-be (u/krw (:cost stats))}
                            {:kpi   "노출수"
                             :to-be (:impressions stats)}
                            {:kpi   "클릭수"
                             :to-be (:clicks stats)}
                            {:kpi   "CPC"
                             :to-be (u/krw (Math/ceil (:cpc stats)))}
                            {:kpi   "클릭률"
                             :to-be (u/pct-fmt 2 (/ (:clicks stats)
                                                    (:impressions stats)))}
                            ;; {:kpi   "전환수"
                            ;;  :to-be (:conversions stats)}
                            ;; {:kpi "전환률"
                            ;;  :to-be 4333}
                            ;; {:kpi "ROAS"
                            ;;  :to-be 129}
                            ])
         :bordered   true
         ;; :size       "middle"
         :pagination false
         :columns    columns
         :rowKey     :kpi}]]]

     [nav-buttons ctx]]))

(def component
  (ui/constructor
    {:renderer step-2
     :component-deps [:conversion-funnel
                      :slider]
     :topic :optimize
     :subscription-deps [:marginals
                         :optimize-settings
                         :optimize-stats]}))
