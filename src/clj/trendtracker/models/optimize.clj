(ns trendtracker.models.optimize
  (:require [clojure.java.jdbc :as jdbc]
            [hugsql.core :as hugsql]
            [jsonista.core :as json]
            [semantic-csv.core :as scsv]
            [trendtracker.config :refer [config]]))

(def db-fns
  (hugsql/map-of-db-fns "sql/optimize.sql"))

(hugsql/def-db-fns "sql/optimize.sql")

(defn customer-estimates
  [customer-id]
  (estimates
   (:db-spec config)
   {:customer-id customer-id}))

(defn settings
  [customer-id]
  (when-let [raw-settings (current-settings (:db-spec config) {:customer-id customer-id})]
    (-> raw-settings
        (update :targets json/read-value)
        (update :objective keyword))))

(defn save-settings
  [customer-id budget objective targets bid-limit]
  (-save-settings
   (:db-spec config)
   {:customer-id customer-id
    :budget budget
    :objective (name objective)
    :targets targets
    :bid-limit bid-limit}))

(defn insert-estimates [customer-id rel]
  (jdbc/with-db-transaction [tx (:db-spec config)]
    (let [chunks (partition-all 3000 rel)]
      (doseq [chunk chunks]
        (println "Inserting" (first chunk) "...")
        (-insert-estimates
         tx
         {:estimates
          (->> chunk
               (scsv/vectorize
                {:header [:key :device :keywordplus :bid :impressions :clicks :cost]
                 :prepend-header false})
               (map #(cons customer-id %)))})))))

(defn insert-click-marginals
  [customer-id rel]
  (jdbc/with-db-transaction [tx (:db-spec config)]
    (let [chunks (partition-all 2000 rel)]
      (doseq [chunk chunks]
        (println "Inserting" (first chunk) "...")
        (-insert-click-marginals
         tx
         {:marginals
          (->> chunk
               (map #(assoc % :customer-id customer-id))
               (scsv/vectorize
                {:prepend-header false
                 :header [:customer-id, :key, :device, :keywordplus, :bid, :impressions,
                          :clicks, :cost, :marginal-bid, :marginal-impressions, :marginal-clicks,
                          :marginal-cost, :marginal-efficiency]}))})))))

(defn fetch-marginals
  [customer-id]
  (-fetch-marginals (:db-spec config) {:customer-id customer-id}))

(comment
  (current-settings (:db-spec config) {:customer-id 1334028})
  (count (fetch-marginals 1334028))
  ;; 9625
  (count (fetch-marginals 137307))
  ;; 1771
  (settings 777309)
  ;; nil
  (settings 137307))


