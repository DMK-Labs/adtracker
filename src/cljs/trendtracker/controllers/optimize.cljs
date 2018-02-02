(ns trendtracker.controllers.optimize
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [trendtracker.utils :as u]))

(def settings
  {:budget 4160000
   :objective :clicks})

(defn set-optimization [app-db settings]
  (assoc-in app-db [:kv :optimize :settings] settings))

(def controller
  (pl-controller/constructor
   {:params (constantly true)
    :start (fn [_ _ app-db]
             (set-optimization app-db settings))}
   {:set (pipeline! [value app-db]
           (pl/commit! (set-optimization app-db settings))
           (dataloader-controller/run-dataloader!))
    :set-budget (pipeline! [value app-db]
                  (pl/commit! (assoc-in app-db [:kv :optimize :settings :budget] value)))
    :sync (pipeline! [value app-db]
            (dataloader-controller/run-dataloader!))}))
