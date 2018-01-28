(ns trendtracker.forms.optimize-objective
  (:require [forms.validator :as v]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [trendtracker.forms.validators :as validators]))

(def validator
  (v/validator
   {:targets [[:not-empty validators/not-empty?]]
    :objective []}))

(defrecord OptimizeObjectiveForm [validator])

(defmethod forms-core/get-data OptimizeObjectiveForm [_ _ _]
  ;; TODO api endpoint for calling this:
  ;; last-optimization-setting-by-client-id
  {:objective :impressions
   :targets ["cmp-a001-01-000000000243172"]})

(defmethod forms-core/submit-data OptimizeObjectiveForm [_ _ _ data]
  (print "Sending form" data))

(defmethod forms-core/on-submit-success OptimizeObjectiveForm [this app-db form-id res]
  (pipeline! [value app-db]
    (pl/redirect! {:page "optimize" :subpage "new" :step "2"})))

(defn constructor []
  (->OptimizeObjectiveForm validator))
