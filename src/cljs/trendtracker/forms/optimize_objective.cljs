(ns trendtracker.forms.optimize-objective
  (:require [forms.validator :as v]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [trendtracker.forms.validators :as validators]))

(def validator
  (v/validator
   {:objective [[:not-empty validators/not-empty?]]
    :targets [[:not-empty validators/not-empty?]]}))

(defrecord OptimizeObjectiveForm [validator])

(defmethod forms-core/submit-data OptimizeObjectiveForm [_ _ form-id data]
  (print "Sending form" form-id))

(defmethod forms-core/on-submit-success OptimizeObjectiveForm [this app-db form-id res]
  (pipeline! [value app-db]
    (pl/redirect! {:page "optimize" :subpage "new" :step "2"})))

(defn constructor []
  (->OptimizeObjectiveForm validator))
