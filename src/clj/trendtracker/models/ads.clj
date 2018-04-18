(ns trendtracker.models.ads
  (:require [hugsql.core :as hugsql]
            [trendtracker.config :refer [config creds]]))

(hugsql/def-db-fns "sql/ads.sql")
(def db-fns (hugsql/map-of-db-fns "sql/ads.sql"))
(declare powerlink-ads)

(comment
 (powerlink-ads (:db-spec config)
                {:customer 137307
                 :low "2018-02-01"
                 :high "2018-03-31"}))

