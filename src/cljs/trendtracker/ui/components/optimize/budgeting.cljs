(ns trendtracker.ui.components.optimize.budgeting
  (:require [keechma.ui-component :as ui]
            [trendtracker.utils :as u]
            [antizer.reagent :as ant]
            [reagent.core :as r]))

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

(def columns
  [{:title "지표" :dataIndex :kpi}
   {:title "지난 30일" :dataIndex :as-is :className "numbers"
    :render #(r/as-element (u/int-fmt %))}
   {:title "예상 30일" :dataIndex :to-be :className "numbers"
    :render #(r/as-element (u/int-fmt %))}
   {:title "변화" :dataIndex :delta :className "numbers"
    :render #(r/as-element
              [:div
               (u/int-fmt %)
               [ant/icon {:type :arrow-up :style {:color :green}}]])}
   {:title "% 변화" :dataIndex :pct-delta
    :render #(r/as-element
              [:div
               (u/pct-fmt 2 %)
               [ant/icon {:type :arrow-up :style {:color :green}}]])}])

(defn gen
  [n]
  [{:name "노출" :as-is 10000 :to-be (/ n 100)}
   {:name "클릭" :as-is 2000 :to-be (/ n 1000)}
   {:name "전환" :as-is 100 :to-be (/ n 10000)}])

(defn totals [budget]
  [:ul.totals
   [:li.total {:style {:border-right "1px solid #e6ebf1"}}
    [:h2 (u/krw budget)]
    [:p "예상 예산"]]
   [:li.total {:style {:border-right "1px solid #e6ebf1"}}
    [:h2 (u/int-fmt (Math/floor (/ budget 232)))]
    [:p "예상 클릭수"]]
   [:li.total
    [:h2 (u/krw 232)]
    [:p "예상 CPC"]]])

(defn slider [budget]
  [ant/slider
   {:min 0
    :max 10000000
    :step 1000
    :marks {2300000 "지난 30일 예산"
            4340910 "CPC: 200"
            6340910 "CPC: 100"
            9000000 "Negative ROI"}
    :onAfterChange #()
    :tipFormatter #(u/krw %)
    :defaultValue budget}])

(defn step-2 [ctx]
  (let [budget 1000000
        conversion-funnel (ui/component ctx :conversion-funnel)]
    (fn []
      [ant/card
       [ant/row
        [ant/col
         [totals budget]]]

       [ant/row {:type "flex" :justify "space-around"}
        [ant/col {:span 18}
         [slider]]]

       [ant/row {:type :flex :justify :space-around}
        [conversion-funnel (gen budget)]]

       [ant/row {:gutter 16 :style {:margin-bottom 16}}
        [ant/col
         [ant/table {:dataSource data
                     :bordered true
                     :size "middle"
                     :pagination false
                     :columns columns
                     :rowKey :kpi}]]]

       [ant/form-item {:style {:margin-bottom 0}}
        [ant/button-group
         [ant/button
          {:on-click #(ui/redirect ctx {:page "optimize" :subpage "new" :step 1})
           :icon "left"}
          "이전"]
         [ant/button
          {:on-click #(ui/redirect ctx {:page "optimize" :subpage "new" :step 3})
           :type "primary"}
          "다음" [ant/icon {:type "right"}]]]]])))

(def component
  (ui/constructor
    {:renderer step-2
     :component-deps [:conversion-funnel]}))
