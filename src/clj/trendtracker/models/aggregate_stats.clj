(ns trendtracker.models.aggregate-stats
  (:require [hugsql.core :as hugsql]
            [trendtracker.config :refer [config creds]]))

(hugsql/def-db-fns "sql/aggregate_stats.sql")
(def db-fns (hugsql/map-of-db-fns "sql/aggregate_stats.sql"))
(declare funnel-by-kwid
         funnel-by-keyword
         recent-keyword-performance)

(defn kwid->funnel [customer-id]
  (transduce
   (map (fn [m] 
          [(:keyword_id m) m]))
   conj
   {}
   (funnel-by-kwid
    (:db-spec config)
    {:customer-id customer-id})))

(defn kw->funnel [customer-id]
  (transduce
   (map (fn [m] [(:keyword m) m]))
   conj
   {}
   (funnel-by-keyword
    (:db-spec config)
    {:customer-id customer-id})))

(comment
  (kwid-ctr-cvr 
   (:db-spec config)
   {:id "nkw-a001-01-000000338280928"})
  (first (kwid->funnel 137307))
  ;; =>
  ;; ["nkw-a001-01-000001088394951"
  ;;  {:keyword_id "nkw-a001-01-000001088394951",
  ;;   :keyword "유아보습로션",
  ;;   :impressions 52,
  ;;   :clicks 0,
  ;;   :conversions 0,
  ;;   :ctr 0.0,
  ;;   :cvr 0.0,
  ;;   :i2c 0.0}]
  (first (kw->funnel 137307))
  ;; =>
  ;; ["폼클렌저"
  ;;  {:keyword "폼클렌저",
  ;;   :impressions 17,
  ;;   :clicks 0,
  ;;   :conversions 0,
  ;;   :ctr 0.0,
  ;;   :cvr 0.0,
  ;;   :i2c 0.0}]
  (by-adgroup
   (:db-spec config)
   {:id "grp-a001-01-000000006315078"
    :low "2018-03-26"
    :high "2018-04-01"})
  (adgroups
   (:db-spec config)
   {:customer-id 137307
    :low "2018-02-26"
    :high "2018-03-15"}))
