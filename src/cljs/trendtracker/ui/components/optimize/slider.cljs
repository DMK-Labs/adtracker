(ns trendtracker.ui.components.optimize.slider
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> <cmd]]
            [trendtracker.utils :as u]))

(defn render [ctx]
  (let [{:keys [budget]} (sub> ctx :optimize-settings)
        max (:cost (last (sub> ctx :ridgeline)))]
    [ant/slider {:min 0
                 :max (- (Math/floor max) 50000)
                 :step 10000
                 :marks (if (> 100000000 max)
                          {1000000 "100만원"
                           10000000 "1,000만원"
                           25000000 "2,500만원"}
                          {10000000 "1,000만원"
                           50000000 "5,000만원"
                           150000000 "15,000만원"})
                 :onAfterChange #(<cmd ctx :sync)
                 :onChange #(<cmd ctx :set-budget %)
                 :tipFormatter #(u/krw %)
                 :value budget}]))

(def component
  (ui/constructor
   {:renderer render
    :topic :optimize
    :subscription-deps [:optimize-settings :ridgeline]}))
