(ns trendtracker.controllers.optimize
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [trendtracker.api :as api]))

(defn set-optimization [app-db settings]
  (assoc-in app-db [:kv :optimize :settings] settings))

(def controller
  (pl-controller/constructor
   {:params (constantly true)}
   {:set (pipeline! [value app-db]
           (pl/commit! (set-optimization app-db value))
           (dataloader-controller/run-dataloader!))
    :set-budget (pipeline! [value app-db]
                  (pl/commit! (assoc-in app-db [:kv :optimize :settings :budget] value)))
    :sync (pipeline! [value app-db]
            (dataloader-controller/run-dataloader!))
    :save (pipeline! [value app-db]
            (api/save-settings
             (-> (get-in app-db [:kv :optimize :settings])
                 (update :targets (comp js/JSON.stringify clj->js))
                 (assoc :customer-id (get-in app-db [:kv :current-client :customer_id])))))}))
