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

(defn parent-id [adgroup-id]
  (:id
    (parent-campaign
      (:db-spec config)
      {:adgroup-id adgroup-id})))

(defn insert-click-marginals!
  [rel]
  (jdbc/with-db-transaction [tx (:db-spec config)]
    (doseq [row rel]
      (println "Inserting" row)
      (insert-click-marginals
       tx
       (-> row
           (assoc :campaign-id (parent-id (:adgroup-id row)))
           (set/rename-keys {:key :keyword-id}))))))

;; (defn marginals [{:keys [naver_customer_id objective] :as settings}]
;;   (let [estimates (customer-estimates naver_customer_id) ;; TODO filter by campaign_id
;;         ]
;;     (mckp/marginal-landscape objective estimates)))

(defn marginals [{:keys [bid-limit]}]
  ;; TODO: replace with a function
  (mckp/marginal-landscape
   :clicks
   (h/where
    {:bid [< (or bid-limit (* 100 1000))]}
    (kw-estimates (:db-spec config) {}))))

(comment
  (let [ma (marginals (settings 137307))]
    (insert-click-marginals! ma))

  (def y (marginals {:bid-limit 2000}))
  (def x (marginals {}))

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
