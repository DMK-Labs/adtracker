(ns trendtracker.models.aggregate-stats
  (:require [hugsql.core :as hugsql]
            [huri.core :as h]
            [trendtracker.config :refer [config creds]]))

(hugsql/def-db-fns "sql/aggregate_stats.sql")
(def db-fns (hugsql/map-of-db-fns "sql/aggregate_stats.sql"))

(defn kwid->funnel [customer-id]
  (transduce
   (map (fn [m] [(:keyword_id m) m]))
   conj
   {}
   (global-funnel
    (:db-spec config)
    {:customer-id customer-id})))

(comment
  (kwid-ctr-cvr (:db-spec config)
                {:id "nkw-a001-01-000000338280928"})
  (kwid->funnel 137307))
