(ns trendtracker.forms.optimize-objective
  (:require [forms.validator :as v]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [trendtracker.forms.validators :as validators]
            [trendtracker.api :as api]
            [promesa.core :as p]
            [keechma.toolbox.ajax :as ajax]))

(def validator
  (v/validator
   {:targets []
    :objective []}))

(defrecord OptimizeObjectiveForm [validator])

(defmethod forms-core/get-data OptimizeObjectiveForm [_ app-db _]
  (pipeline! [_ app-db]
    (dataloader-controller/wait-dataloader-pipeline!)
    (api/optimize-settings (get-in app-db [:kv :current-client :customer_id]))))

(defmethod forms-core/process-in OptimizeObjectiveForm [_ _ _ data]
  (update
   (select-keys data [:targets :objective])
   :targets
   js/JSON.parse))

;; (defmethod forms-core/submit-data OptimizeObjectiveForm [_ _ _ data]
;;   (ajax/GET "/api/optimize/marginals" {}))

;; (defmethod forms-core/on-submit-success OptimizeObjectiveForm [this app-db form-id res]
;;   (pipeline! [value app-db]
;;     (pl/commit! (assoc-in app-db [:kv :optimize :marginals] res))
;;     (pl/redirect! {:page "optimize"
;;                    :subpage "settings"
;;                    :step "2"
;;                    :client (get-in app-db [:route :data :client])})))

(defn constructor []
  (->OptimizeObjectiveForm validator))
