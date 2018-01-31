(ns trendtracker.models.optimize
  (:require [clojure.java.jdbc :as jdbc]
            [clojure.set :as set]
            [hugsql.core :as hugsql]
            [huri.core :as h]
            [jsonista.core :as json]
            [optimus.mckp :as mckp]
            [trendtracker.config :refer [config]]))

(def db-fns
  (hugsql/map-of-db-fns "sql/optimize.sql"))

(hugsql/def-db-fns "sql/optimize.sql")

(defn customer-estimates
  [customer-id]
  (estimates (:db-spec config)
             {:customer-id customer-id}))

(defn settings
  [customer-id]
  (-> (current-settings (:db-spec config) {:customer-id customer-id})
      (update :targets json/read-value)
      (update :objective keyword)))

(defn marginals [{:keys [budget naver_customer_id objective] :as settings}]
  (let [estimates (customer-estimates naver_customer_id) ;; TODO filter by campaign_id
        ]
    (mckp/marginal-landscape objective estimates)))

(defn parent [adgroup-id]
  (parent-campaign
   (:db-spec config)
   {:adgroup-id adgroup-id}))

(defn insert-click-marginals!
  [rel]
  (jdbc/with-db-transaction [tx (:db-spec config)]
    (doseq [marginals rel]
      (println "Inserting" marginals)
      (insert-click-marginals
       tx
       (-> marginals
           (assoc :campaign-id (:id (parent (:adgroup-id marginals))))
           (set/rename-keys {:key :keyword-id}))))))

(def marginals
  ;; TODO: replace with a function
  (mckp/marginal-landscape :clicks (kw-estimates (:db-spec config) {})))

(comment
  (let [ma (marginals (settings 137307))]
    (insert-click-marginals! ma))

  (def df (read-string (slurp "data/df")))
  (count df)
  ;; => 56162

  (doseq [ests (partition-all
                1000
                (map (juxt :key :device :keywordplus :bid :impressions :clicks :cost) df))]
    (print "inserting" ests)
    (insert-kw-estimates
     (:db-spec config)
     {:estimates ests})))
