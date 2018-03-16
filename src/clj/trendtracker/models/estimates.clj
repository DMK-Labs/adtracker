(ns trendtracker.models.estimates
  (:require [hugsql.core :as hugsql]
            [trendtracker.config :refer [config creds]]))

(hugsql/def-db-fns "sql/estimates.sql")
(def db-fns (hugsql/map-of-db-fns "sql/estimates.sql"))

(defn by-customer [id]
  (by-customer-id
   (:db-spec config)
   {:id id}))

(comment
  (count
   (by-customer-id (:db-spec config)
                   {:id 137307})))
