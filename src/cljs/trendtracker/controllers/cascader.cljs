(ns trendtracker.controllers.cascader
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]))

(def controller
  (pl-controller/constructor
   (constantly true)
   {:start (pipeline! [_ _]
             (pl/execute! :set ["total"]))
    :set (pipeline! [value app-db]
           (pl/commit!
            (assoc-in app-db [:kv :cascader] value))
           (dataloader-controller/run-dataloader!))}))
