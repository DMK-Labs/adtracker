(ns trendtracker.controllers.date-range
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]))

(defn preset
  [count]
  [(.startOf (.subtract (js/moment) count "d") "d")
   (.startOf (.subtract (js/moment) 1 "d") "d")])

(defn presets []
  {:last-7-days (preset 7)
   :last-14-days (preset 14)
   :last-28-days (preset 28)
   :last-90-days (preset 90)
   :last-month [(.startOf (.subtract (js/moment) 1 "M") "M")
                (.endOf (.subtract (js/moment) 1 "M") "M")]})

(defn prev-range [[low high]]
  (let [num-days (inc (.diff high low "days"))]
    [(.subtract (js/moment low) num-days "d")
     (.subtract (js/moment low) 1 "d")]))

(def controller
  (pl-controller/constructor
   (constantly true)
   {:start (pipeline! [_ _]
             (pl/execute! :set (preset 90)))
    :set (pipeline! [value app-db]
           (pl/commit!
            (assoc-in app-db [:kv :date-range]
                      {:curr value
                       :prev (prev-range value)}))
           (dataloader-controller/run-dataloader!))}))
