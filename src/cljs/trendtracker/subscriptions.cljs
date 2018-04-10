(ns trendtracker.subscriptions
  (:require [trendtracker.datasources :refer [datasources]]
            [keechma.toolbox.forms.helpers :as forms-helpers])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn get-kv [key]
  (fn [app-db-atom]
    (reaction
     (get-in @app-db-atom (flatten [:kv key])))))

(def subscriptions
  {:form-state forms-helpers/form-state-sub
   :keyword-tool (get-kv :keyword-tool)
   :ridgeline (get-kv [:optimize :ridgeline])})
