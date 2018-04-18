(ns trendtracker.models.daily-stats
  (:require [hugsql.core :as hugsql]
            [huri.core :as h]
            [plumbing.core :refer [defnk fnk sum]]
            [trendtracker.config :refer [config creds]]
            [clojure.java.jdbc :as jdbc]))

(hugsql/def-db-fns "sql/daily_stats.sql")
(def db-fns (hugsql/map-of-db-fns "sql/daily_stats.sql"))

(defn add-ratios2 [{:keys [cost clicks revenue impressions conversions ad_rank_sum] :as m}]
  (assoc m
    :avg-rank (when (pos? impressions)
                (/ ad_rank_sum (float impressions)))
    :cpc (when (pos? clicks) (/ cost clicks))
    :roas (if (zero? cost)
            0
            (/ revenue (float cost)))
    :cpm (when (pos? impressions) (/ cost (/ impressions 1000)))
    :cpa (when (pos? conversions) (/ cost conversions))
    :ctr (if (zero? impressions)
           0
           (/ clicks (float impressions)))
    :cvr (if (zero? clicks)
           0
           (/ conversions (float clicks)))
    :i2c (if (zero? impressions)
           0
           (/ conversions (float impressions)))))

(declare first-recorded-performance
         by-adgroup
         by-campaign
         by-customer
         by-type)

