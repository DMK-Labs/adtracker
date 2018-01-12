(ns trendtracker.ui.pages.dashboard
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  (let [breadcrumbs (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)
        snapshot (ui/component ctx :snapshot)
        tabbed-charts (ui/component ctx :tabbed-charts)]
    [:div
     [:div.content-header
      [breadcrumbs]
      [ant/row
       [:h2 "검색광고 대쉬보드"]]
      [ant/row {:gutter 16 :type "flex" :justify "space-between"}
       [ant/col
        [:div
         [ant/icon {:type "filter" :style {:margin-right 8}}]
         [ant/cascader]]]
       [ant/col
        [date-range-picker]]]]

     [:div.content
      [ant/row {:gutter 16}
       [ant/col {:md 8 :sm 12} [snapshot "비용" :cost "#fa541c"]]
       [ant/col {:md 8 :sm 12} [snapshot "매출" :revenue "#52c41a"]]
       [ant/col {:md 8 :sm 12} [snapshot "ROAS" :roas "#B5A1DE"]]]
      [ant/row [tabbed-charts]]
      [ant/row {:gutter 16}
       ]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:date-range-picker
                     :breadcrumbs
                     :snapshot
                     :tabbed-charts]}))
