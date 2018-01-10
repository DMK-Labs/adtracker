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
      [(ui/component ctx :date-range-picker)]]]]])

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:date-range-picker
                     :breadcrumbs]}))
