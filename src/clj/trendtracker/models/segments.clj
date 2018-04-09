(ns trendtracker.models.segments
  (:require [hugsql.core :as hugsql]
            [trendtracker.config :refer [config creds]]))

(hugsql/def-db-fns "sql/segments.sql")

(comment
 (hugsql/map-of-db-fns "sql/segments.sql")

 ;; TODO: Make this also take campaign-id or adgroup-id (same filtering as the daily stats)
 (pc-mobile
  (:db-spec config)
  {:customer-id 137307
   :low         "2018-02-01"
   :high        "2018-02-28"})

 (keywords
  (:db-spec config)
  {:customer-id 137307
   :low         "2018-02-01"
   :high        "2018-02-28"}))
