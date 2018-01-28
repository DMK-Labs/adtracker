(ns trendtracker.controllers.optimize
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]))

(def settings
  {:budget 3139001
   :objective :clicks
   :cost 3039000
   :impressions 1939292
   :clicks 19104
   :conversions 230})

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
