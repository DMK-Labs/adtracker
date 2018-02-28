(ns trendtracker.subscriptions
  (:require [keechma.toolbox.dataloader.subscriptions :as dataloader]
            [trendtracker.datasources :refer [datasources]]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [trendtracker.edb :refer [edb-schema]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn get-kv [key]
  (fn [app-db-atom]
    (reaction
     (get-in @app-db-atom (flatten [:kv key])))))

(def subscriptions
  (merge {:form-state forms-helpers/form-state-sub
          :keyword-tool (get-kv :keyword-tool)
          :ridgeline (get-kv [:optimize :ridgeline])}
         (dataloader/make-subscriptions datasources edb-schema)))
