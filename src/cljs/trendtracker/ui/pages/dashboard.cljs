(ns trendtracker.ui.pages.dashboard
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  (let [breadcrumbs       (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)
        snapshot          (ui/component ctx :snapshot)
        tabbed-charts     (ui/component ctx :tabbed-charts)
        cascader          (ui/component ctx :cascader)
        scatter-chart     (ui/component ctx :scatter-chart)]
    [:div
     [:div.content-header
      [breadcrumbs]
      [ant/row
       [:h1 "검색광고 대쉬보드"]]
      [ant/row {:gutter 16 :type "flex" :justify "space-between"}
       [ant/col
        [cascader]]
       [ant/col
        [date-range-picker]]]]

     [:div.content
      [ant/row {:gutter 16}
       [ant/col {:md 8 :sm 12} [snapshot :cost]]
       [ant/col {:md 8 :sm 12} [snapshot :revenue]]
       [ant/col {:md 8 :sm 12} [snapshot :roas]]]
      [ant/row [tabbed-charts]]
      ;; [ant/row [scatter-chart]]
      [ant/row {:gutter 16}
       [ant/col {:md 8 :sm 12} [snapshot :cpc]]
       [ant/col {:md 8 :sm 12} [snapshot :cpm]]
       [ant/col {:md 8 :sm 12} [snapshot :cpa]]]
      ]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:date-range-picker
                     :breadcrumbs
                     :snapshot
                     :tabbed-charts
                     :cascader
                     :scatter-chart]}))
