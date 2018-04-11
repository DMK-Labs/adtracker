(ns trendtracker.models.insights
  (:require [hugsql.core :as hugsql]
            [trendtracker.config :refer [config creds]]))

(hugsql/def-db-fns "sql/insights.sql")
(def db-fns (hugsql/map-of-db-fns "sql/insights.sql"))

(declare no-clicks
         biggest-losers
         best-powerlink-ads)

(comment
 (no-clicks (:db-spec config)
            {:customer 137307})
 (biggest-losers (:db-spec config)
                 {:customer 137307})
 (time (best-powerlink-ads (:db-spec config)
                           {:customer 137307})))
