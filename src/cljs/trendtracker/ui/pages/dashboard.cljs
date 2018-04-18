(ns trendtracker.ui.pages.dashboard
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> route>]]
            [reagent.core :as r]
            [trendtracker.ui.components.common :as common]))

(defn render [ctx]
  (let [cost-snapshot-key (r/atom :budget)]
    (fn []
      (let [breadcrumbs (ui/component ctx :breadcrumbs)
            date-range-picker (ui/component ctx :date-range-picker)
            snapshot (ui/component ctx :snapshot)
            tabbed-charts (ui/component ctx :tabbed-charts)
            cascader (ui/component ctx :cascader)
            segment-stats (ui/component ctx :segment-stats)
            daily-stats (sub> ctx :daily-stats)
            no-conversions? (every? zero? (map :conversions (concat (:curr daily-stats) (:prev daily-stats))))]
       [:div
        [common/content-header
         [breadcrumbs]
         [ant/row {:type "flex" :justify "space-between"}
          [ant/col {:md 7 :xs 24}
           [:h2.page-title "검색광고 대쉬보드"]]
          [ant/col
           [ant/row {:type "flex" :justify "space-between" :gutter 8}
            [ant/col [cascader]]
            [ant/col [date-range-picker]]]]]]
        [:div.content
         (if no-conversions?
           [ant/row {:gutter 16}
            [ant/col {:md 8 :sm 24}
             [:div {:on-click #(swap! cost-snapshot-key
                                      (fn [k] (if (= :budget k)
                                                :cost
                                                :budget)))}
              [snapshot @cost-snapshot-key]]]
            [ant/col {:md 8 :sm 12} [snapshot :cpc]]
            [ant/col {:md 8 :sm 12} [snapshot :cpm]]]
           [ant/row {:gutter 16}
            [ant/col {:sm 8}
             [:div {:on-click #(swap! cost-snapshot-key
                                      (fn [k] (if (= :budget k)
                                                :cost
                                                :budget)))}
              [snapshot @cost-snapshot-key]]]
            [ant/col {:sm 8} [snapshot :revenue]]
            [ant/col {:sm 8} [snapshot :roas]]])
         [ant/row {:gutter 16}
          [ant/col {:md 16} [tabbed-charts]]
          [ant/col {:md 8} [(ui/component ctx :cost-effectiveness)]]]
         [ant/row {:gutter 16}
          [ant/col {:lg 24} [(ui/component ctx :device-pie)]]]
         [ant/row [ant/col [segment-stats]]]]]))))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:date-range-picker
                     :breadcrumbs
                     :snapshot
                     :tabbed-charts
                     :segment-stats
                     :cascader
                     :device-pie
                     :cost-effectiveness]
    :subscription-deps [:daily-stats]}))
