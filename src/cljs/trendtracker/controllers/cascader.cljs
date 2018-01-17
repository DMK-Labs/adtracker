(ns trendtracker.controllers.cascader
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]))

(defn set-cascader [app-db value]
  (assoc-in app-db [:kv :cascader] value))

(def controller
  (pl-controller/constructor
    {:params (constantly true)
     :start (fn [_ _ app-db]
              (set-cascader app-db ["total"]))}
    {:set (pipeline! [value app-db]
            (pl/commit! (set-cascader app-db value))
            (dataloader-controller/run-dataloader!))}))
