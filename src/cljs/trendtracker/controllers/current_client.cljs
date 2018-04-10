(ns trendtracker.controllers.current-client
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [trendtracker.edb :refer [get-item-by-id]]
            [antizer.reagent :as ant]))

(def controller
  (pl-controller/constructor
   (constantly true)
   {:set (pipeline! [value app-db]
           (pl/commit! (assoc-in app-db [:kv :portfolio-optimizing] nil))
           (pl/commit! (assoc-in app-db [:kv :portfolio] nil))
           (pl/commit! (assoc-in app-db [:kv :optimize :stats] nil))
           (pl/redirect! (assoc (get-in app-db [:route :data])
                                :client value))
           (pl/commit! (assoc-in app-db
                                 [:kv :current-client]
                                 (get-item-by-id app-db :managed-clients value)))
           (pl/commit! (assoc-in app-db [:kv :cascader] ["total"]))
           (do (dataloader-controller/run-dataloader!)
               (ant/message-success (str "광고 계정 변경: " value))))}))
