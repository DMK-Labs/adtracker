(ns trendtracker.ui.components.optimize.detail
  (:require [keechma.ui-component :as ui]
            [antizer.reagent :as ant]))

(defn render [ctx]
  [ant/row {:style {:margin-bottom 16}}
   [ant/table
    {:scroll {:x 1200}
     :columns
     (map (fn [m] (assoc m :key (:title m)))
          [{:title "Type"}
           {:title "Campaign"}
           {:title "Campaign ID"}
           {:title "Adgroup"}
           {:title "Adgroup ID"}
           {:title "Impressions"}
           {:title "Clicks"}
           {:title "Cost"}
           {:title "Ad Rank Sum"}
           {:title "Conversions"}
           {:title "Revenue"}])}]])

(def component
  (ui/constructor
    {:renderer render}))
