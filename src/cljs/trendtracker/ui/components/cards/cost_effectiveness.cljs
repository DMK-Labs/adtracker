(ns trendtracker.ui.components.cards.cost-effectiveness
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [trendtracker.utils :as u]
            [keechma.toolbox.ui :refer [sub>]]
            [trendtracker.ui.components.common :as common]))

(defn render [ctx]
  (let [stats (sub> ctx :daily-stats)
        current (:curr stats)
        previous (:prev stats)
        cost (u/sum :cost current)
        p-cost (u/sum :cost previous)
        clicks (u/sum :clicks current)
        p-clicks (u/sum :clicks previous)
        impressions (u/sum :impressions current)
        p-impressions (u/sum :impressions previous)
        conversions (u/sum :conversions current)
        p-conversions (u/sum :conversions previous)
        col-opts {:xs 8 :md 12 :style {:margin-bottom 12}}]
    [ant/card {:title "비용 & 효율"
               :loading (= :pending (:status (sub> ctx :daily-stats-meta)))
               :style {:height 355}}
     (map
      #(into [ant/row {:gutter 16 :key (first %)}] %)
      (partition-all
       2
       (map (fn [{:keys [title stat p-stat formatter down-is-good?]}]
              [ant/col (assoc col-opts :key title)
               [:h3 {:style {:margin-bottom 0 :color "#666"}} title]
               [:span
                [:h2 {:style {:margin-bottom 0}}
                 (formatter stat)]
                (common/delta-widget
                 (u/delta p-stat stat)
                 down-is-good?)]])
            [{:title "CPM"
              :stat (int (/ cost (/ impressions 1000)))
              :p-stat (int (/ p-cost (/ p-impressions 1000)))
              :formatter u/krw
              :down-is-good? true}
             {:title "클릭률"
              :stat (/ clicks impressions)
              :p-stat (/ p-clicks p-impressions)
              :formatter u/pct-fmt
              :down-is-good? false}
             {:title "CPC"
              :stat (int (/ cost clicks))
              :p-stat (int (/ p-cost p-clicks))
              :formatter u/krw
              :down-is-good? true}
             {:title "전환률"
              :stat (/ conversions clicks)
              :p-stat (/ p-conversions p-clicks)
              :formatter u/pct-fmt
              :down-is-good? false}
             {:title "CPA"
              :stat (int (/ cost conversions))
              :p-stat (int (/ p-cost p-conversions))
              :formatter u/krw
              :down-is-good? true}
             {:title "I2C"
              :stat (/ conversions impressions)
              :p-stat (/ p-conversions p-impressions)
              :formatter u/pct-fmt
              :down-is-good? false}])))]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:daily-stats :daily-stats-meta]}))
