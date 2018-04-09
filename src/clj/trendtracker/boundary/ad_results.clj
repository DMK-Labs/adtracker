(ns trendtracker.boundary.ad-results
  (:require [clojure.java.jdbc :as jdbc]
            [hugsql.core :as hugsql]
            [trendtracker.config :refer [config]])
  (:import (org.postgresql.copy CopyManager)))

(hugsql/def-db-fns "sql/daily_stats.sql")

(defn conn []
  (jdbc/get-connection (:db-spec config)))

(defn manager []
  (CopyManager. (conn)))

(defn append-copy-to!
  [table-name buf]
  (.copyIn (manager)
           (str "COPY " table-name " FROM stdin WITH NULL AS '' DELIMITER '\t'")
           buf))

(defn append [table buf]
  (append-copy-to! table buf))

(defn append-conversions [buf]
  (append "naver.conversion" buf))

(defn append-effectiveness [buf]
  (append "naver.effectiveness" buf))

(defn last-recorded [{:keys [customer-id table] :as q}]
  (last-recorded-foo (:db-spec config) q))
