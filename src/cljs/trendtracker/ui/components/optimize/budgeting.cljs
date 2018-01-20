(ns trendtracker.ui.components.optimize.budgeting
  (:require [keechma.ui-component :as ui]
            [trendtracker.utils :as u]
            [antizer.reagent :as ant]
            [reagent.core :as r]))

(defn gen
  [n]
  [{:name "노출" :as-is 10000 :to-be (/ n 100)}
   {:name "클릭" :as-is 2000 :to-be (/ n 1000)}
   {:name "전환" :as-is 100 :to-be (/ n 10000)}])

(defn step-2 [ctx]
  (let [budget 1000000
        conversion-funnel (ui/component ctx :conversion-funnel)]
    (fn []
      [:div
       [ant/row [ant/col [:ul.totals
                          [:li.total {:style {:border-right "1px solid #e6ebf1"}}
                           [:h2 (u/krw budget)]
                           [:p "예상 예산"]]
                          [:li.total {:style {:border-right "1px solid #e6ebf1"}}
                           [:h2 (u/int-fmt (Math/floor (/ budget 232)))]
                           [:p "예상 클릭수"]]
                          [:li.total
                           [:h2 (u/krw 232)]
                           [:p "예상 CPC"]]]]]
       [ant/row {:type "flex" :justify "space-around"}
        [ant/col {:span 20}
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
           :defaultValue budget}]]]
       [ant/row {:type :flex :justify :space-around}
        [conversion-funnel (gen budget)]]
       [ant/row {:gutter 16}
        [ant/col {:xl 12 :lg 24}
         [ant/card
          [ant/table
           {:dataSource (mapv #(assoc % :delta (- (:to-be %) (:as-is %))
                                        :pct-delta (u/delta (:as-is %) (:to-be %)))
                              [{:type "노출수" :as-is 66219 :to-be 87109}
                               {:type "클릭수" :as-is 3910 :to-be 4333}
                               {:type "전환수" :as-is 910 :to-be 1120}
                               {:type "CPC" :as-is 320 :to-be 129}])
            :pagination false
            :columns [{:title "Type" :dataIndex :type}
                      {:title "지난 30일" :dataIndex :as-is :className "numbers"
                       :render #(r/as-element (u/int-fmt %))}
                      {:title "예상 30일" :dataIndex :to-be :className "numbers"
                       :render #(r/as-element (u/int-fmt %))}
                      {:title "변화" :dataIndex :delta :className "numbers"
                       :render #(r/as-element [:div
                                               (u/int-fmt %)
                                               [ant/icon {:type :arrow-up :style {:color :green}}]])}
                      {:title "% 변화" :dataIndex :pct-delta
                       :render #(r/as-element
                                  [:div
                                   (u/pct-fmt 2 %)
                                   [ant/icon {:type :arrow-up :style {:color :green}}]])}]
            :rowKey "type"}]]]
        #_[ant/col {:xl 12 :lg 24}
           [ant/card
            [ant/table
             {:dataSource (mapv #(assoc % :delta (- (:to-be %) (:as-is %))
                                          :pct-delta (u/delta (:as-is %) (:to-be %)))
                                [{:type "클릭률" :as-is 66219 :to-be 87109}
                                 {:type "전환률" :as-is 3910 :to-be 4333}
                                 {:type "ROAS" :as-is 320 :to-be 129}])
              :pagination false
              :columns [{:title "Type" :dataIndex :type}
                        {:title "지난 30일" :dataIndex :as-is :className "numbers"
                         :render #(r/as-element (u/int-fmt %))}
                        {:title "예상 30일" :dataIndex :to-be :className "numbers"
                         :render #(r/as-element (u/int-fmt %))}
                        {:title "변화" :dataIndex :delta :className "numbers"
                         :render #(r/as-element
                                    [:div
                                     (u/int-fmt %)
                                     [ant/icon {:type :arrow-up :style {:color :green}}]])}
                        {:title "변화 %" :dataIndex :pct-delta
                         :render #(r/as-element
                                    [:div
                                     (u/pct-fmt 2 %)
                                     [ant/icon {:type :arrow-up :style {:color :green}}]])}]}]]]]])))

(def component
  (ui/constructor
    {:renderer step-2
     :component-deps [:conversion-funnel]}))
