(ns trendtracker.ui.components.optimize.slider
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> <cmd]]
            [trendtracker.utils :as u]))

(defn render [ctx]
  (let [{:keys [budget]} (sub> ctx :optimize-settings)
        max (u/sum :marginal-cost (sub> ctx :marginals))]
    [ant/slider {:min 0
                 :max (Math/floor (/ max 3))
                 :step 5000
                 :marks {5000000 "500만원"
                         10000000 "1,000만원"
                         15000000 "1,500만원"}
                 :onAfterChange #(<cmd ctx :sync)
                 :onChange #(<cmd ctx :set-budget %)
                 :tipFormatter #(u/krw %)
                 :value budget}]))

(def component
  (ui/constructor
   {:renderer          render
    :topic             :optimize
    :subscription-deps [:optimize-settings
                        :marginals]}))
