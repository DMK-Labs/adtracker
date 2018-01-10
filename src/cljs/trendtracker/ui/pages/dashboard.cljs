(ns trendtracker.ui.pages.dashboard
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]))

(defn render [ctx]
  [:div
   [:div.content-header
    [(ui/component ctx :breadcrumbs)]
    [:h2 "검색광고 대쉬보드"]
    [ant/row {:gutter 16 :type "flex" :justify "space-between"}
     [ant/col
      [:div
       [ant/icon {:type "filter" :style {:margin-right 8}}]
       [ant/cascader]]]
     [ant/col
      [(ui/component ctx :date-range-picker)]]]]

   [:div.content
    [ant/row {:gutter 16}
     [ant/col {:md 8 :sm 12}
      [(ui/component ctx :kpi-snapshot) "비용" :cost "#fa541c"]]
     [ant/col {:md 8 :sm 12}
      [(ui/component ctx :kpi-snapshot) "매출" :revenue "#52c41a"]]
     [ant/col {:md 8 :sm 12}
      [(ui/component ctx :kpi-snapshot) "ROAS" :roas "#B5A1DE"]]]]])

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:date-range-picker
                     :breadcrumbs
                     :kpi-snapshot]}))
