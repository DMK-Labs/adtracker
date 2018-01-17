(ns trendtracker.subscriptions
  (:require [keechma.toolbox.dataloader.subscriptions :as dataloader]
            [trendtracker.datasources :refer [datasources]]
            [trendtracker.edb :refer [edb-schema]])
  (:require-macros [reagent.ratom :refer [reaction]]))

(defn get-kv [key]
  (fn [app-db-atom]
    (reaction
     (get-in @app-db-atom (flatten [:kv key])))))

(def subscriptions
  (merge (dataloader/make-subscriptions datasources edb-schema)
         {}))
