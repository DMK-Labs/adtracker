(ns trendtracker.db
  (:require [hugsql.core :as hugsql])
  (:import [system.components.jdbc JDBCDatabase]))

(def db-fns
  (hugsql/map-of-db-fns "sql/queries.sql"))

(hugsql/def-db-fns "sql/queries.sql")

(defprotocol SearchadPersistence
  (adgrp-perfs [this] "Select adgroup performances")
  (cmp-perfs [this] "Select adgroup performances")
  (total-perfs [this] "Select adgroup performances"))

(extend-type JDBCDatabase
  SearchadPersistence
  (adgrp-perfs [this params]
    (adgrp-perf-by-id-date this params))
  (cmp-perfs [this params]
    (cmp-perf-by-id-date this params))
  (total-perfs [this params]
    (total-perf-by-date this params)))
