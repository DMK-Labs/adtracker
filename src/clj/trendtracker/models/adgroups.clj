(ns trendtracker.models.adgroups
  (:require [hugsql.core :as hugsql]
            [huri.core :as h]
            [trendtracker.config :refer [config creds]]
            [trendtracker.models.campaigns :as campaigns]))

(def db-fns
  (hugsql/map-of-db-fns "sql/adgroups.sql"))

(hugsql/def-db-fns "sql/adgroups.sql")

(defn parent-id [adgroup-id]
  (:id
   (parent-campaign
    (:db-spec config)
    {:adgroup-id adgroup-id})))

(defn parents-device [adgroup-id]
  (-> adgroup-id
      parent-id
      campaigns/target-device))

(comment
  (time (parents-device "grp-a001-01-000000005801738")))
