(ns trendtracker.models.campaigns
  (:require [hugsql.core :as hugsql]
            [trendtracker.config :refer [config creds]]
            [huri.core :as h]))

(def db-fns (hugsql/map-of-db-fns "sql/campaigns.sql"))

(hugsql/def-db-fns "sql/campaigns.sql")

(defn all [customer-id]
  (-all (:db-spec config) {:customer-id customer-id}))

(def target-device
  { ;; syspharm
   "cmp-a001-01-000000000979011" :pc
   "cmp-a001-01-000000000979827" :mobile

   ;; capharm
   "cmp-a001-01-000000001015620" :pc
   "cmp-a001-01-000000001015651" :mobile})

(comment
  (all (:db-spec config) {:customer-id 137307}))


