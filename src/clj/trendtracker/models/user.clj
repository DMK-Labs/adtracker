(ns trendtracker.models.user
  (:require [hugsql.core :as hugsql]
            [trendtracker.config :refer [config]]))

(:db-spec config)

(def db-fns
  (hugsql/map-of-db-fns "sql/user.sql"))

(hugsql/def-db-fns "sql/user.sql")
