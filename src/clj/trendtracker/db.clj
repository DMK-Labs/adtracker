(ns trendtracker.db
  (:require [hugsql.core :as hugsql]
            [taoensso.nippy :as nippy])
  (:import system.components.jdbc.JDBCDatabase))

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

;; Events API



(defn send-event [db {:keys [type data] :as args}]
  (send-event* db (update args :data nippy/freeze)))

(defn events [db {:keys [type] :as args}]
  (map (fn [m]
         (update m :data nippy/thaw))
       (events* db args)))

(comment
  (send-event
   (:db system.repl/system)
   {:type "optimize"
    :data {:user "dashcrab"
           :objective :clicks
           :budget 1000000
           :targets ["cmp-1" "cmp-2"]}})
  (events (:db system.repl/system) {:type "optimize"}))
