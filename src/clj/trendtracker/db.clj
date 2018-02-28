(ns trendtracker.db
  (:require [hugsql.core :as hugsql]
            [taoensso.nippy :as nippy])
  (:import system.components.jdbc.JDBCDatabase))

(def db-fns
  (hugsql/map-of-db-fns "sql/queries.sql"))

(hugsql/def-db-fns "sql/queries.sql")

