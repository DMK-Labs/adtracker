(ns trendtracker.controllers.date-range
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]))

(defn preset
  [count]
  [(.startOf (.subtract (js/moment) count "d") "d")
   (.startOf (.subtract (js/moment) 1 "d") "d")])

(defn presets []
  {"지난 7일" (preset 7)
   "지난 14일" (preset 14)
   "지난 28일" (preset 28)
   "지난 90일" (preset 90)
   "지난 달" [(.startOf (.subtract (js/moment) 1 "M") "M")
              (.endOf (.subtract (js/moment) 1 "M") "M")]})

(defn prev-range [[low high]]
  (let [num-days (inc (.diff high low "days"))]
    [(.subtract (js/moment low) num-days "d")
     (.subtract (js/moment low) 1 "d")]))

(defn set-ranges [app-db curr]
  (assoc-in app-db [:kv :date-range]
            {:curr curr
             :prev (prev-range curr)}))

(def controller
  (pl-controller/constructor
    {:params (constantly true)
     :start (fn [_ _ app-db]
              (set-ranges app-db (preset 90)))}
    {:set (pipeline! [value app-db]
            (pl/commit! (set-ranges app-db value))
            (dataloader-controller/run-dataloader!))}))
