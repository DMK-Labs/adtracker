(ns trendtracker.models.daily-stats
  (:require [hugsql.core :as hugsql]
            [huri.core :as h]
            [plumbing.core :refer [defnk fnk sum]]
            [plumbing.graph :as graph]
            [trendtracker.config :refer [config creds]]))

(hugsql/def-db-fns "sql/daily_stats.sql")
(def db-fns (hugsql/map-of-db-fns "sql/daily_stats.sql"))

(defn add-ratios2 [{:keys [cost clicks revenue impressions conversions ad_rank_sum during] :as m}]
  (assoc
   m
    :avg-rank (when (pos? impressions)
                (/ ad_rank_sum (double impressions)))
   :cpc (when (pos? clicks) (/ cost clicks))
   :roas (if-not (zero? cost)
           (/ revenue (double cost))
           0)
   :cpm (when (pos? impressions) (/ cost (/ impressions 1000)))
   :cpa (when (pos? conversions) (/ cost conversions))
   :ctr (if-not (zero? impressions)
          (/ clicks (double impressions))
          0)
   :cvr (if-not (zero? clicks)
          (/ conversions (double clicks))
          0)
   :i2c (if-not (zero? impressions)
          (/ conversions (double impressions))
          0)))

(defn add-ratios [rel]
  (h/derive-cols
   {:cpc (fnk [cost clicks]
              (when (pos? clicks)
                (/ cost clicks)))
    :roas (fnk [cost revenue]
               (if (pos? cost)
                 (/ revenue cost)
                 0))
    :cpm (fnk [cost impressions]
              (when (pos? impressions)
                (/ cost
                   (/ impressions 1000))))
    :cpa (fnk [cost conversions]
              (when (pos? conversions)
                (/ cost conversions)))
    :ctr (fnk [clicks impressions]
              (if (zero? impressions)
                0
                (double (/ clicks impressions))))
    :cvr (fnk [conversions clicks]
              (if (zero? clicks)
                0
                (double (/ conversions clicks))))
    :i2c (fnk [conversions impressions]
              (if (zero? impressions)
                0
                (double (/ conversions impressions))))}
                 rel))

(add-ratios2
 {:during #inst "2018-02-26T15:00:00.000-00:00",
  :cost 62550.0,
  :impressions 45645M,
  :clicks 44M,
  :ad_rank_sum 139939M,
  :conversions 21M,
  :revenue 390600M})

(comment
  (map add-ratios2
   (by-customer
    (:db-spec config)
    {:customer-id 137307}))
  (by-adgroup
   (:db-spec config)
   {:adgroup-id "grp-a001-01-000000005994190"
    :low "2018-02-21"
    :high "2018-02-27"
    :customer-id 137307})
  (by-campaign
   (:db-spec config)
   {:campaign-id "cmp-a001-01-000000001015651"
    :low "2018-02-21"
    :high "2018-02-27"
    :customer-id 137307})
  (by-type
   {:campaign-type "powerlink"
    :low "2018-02-21" :high "2018-02-27"
    :customer-id 137307}))
