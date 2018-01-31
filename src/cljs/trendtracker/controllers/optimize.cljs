(ns trendtracker.controllers.optimize
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]))

(def settings
  {:budget 0
   :objective :clicks
   :cost 0
   :impressions 0
   :clicks 0
   :conversions 0})

(defn set-optimization [app-db settings]
  (assoc-in app-db [:kv :optimize :settings] settings))

(def controller
  (pl-controller/constructor
   {:params (constantly true)
    :start (fn [_ _ app-db]
             (set-optimization app-db settings))}
   {:set (pipeline! [value app-db]
           (pl/commit! (set-optimization app-db settings))
           (dataloader-controller/run-dataloader!))}))
