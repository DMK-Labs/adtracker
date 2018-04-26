(ns trendtracker.models.master-reports
  (:require [hugsql.core :as hugsql]
            [huri.core :as h]
            [plumbing.core :refer [defnk fnk sum]]
            [trendtracker.config :refer [config creds]]
            [clojure.java.jdbc :as jdbc]
            [taoensso.timbre :as timbre]))

(hugsql/def-db-fns "sql/master_reports.sql")
(def db-fns (hugsql/map-of-db-fns "sql/master_reports.sql"))

(declare upsert-campaign
         upsert-business-channel
         upsert-adgroup
         upsert-keyword
         upsert-ad)

;; Upserts
(defn upsert-campaigns [rel]
  (jdbc/with-db-connection [conn (:db-spec config)]
    (doseq [campaign rel]
      (upsert-campaign conn campaign))))

(defn upsert-business-channels [rel]
  (jdbc/with-db-connection [conn (:db-spec config)]
    (doseq [business-channel rel]
      (upsert-business-channel conn business-channel))))

(defn upsert-adgroups [rel]
  (jdbc/with-db-connection [conn (:db-spec config)]
    (doseq [adgroup rel]
      (upsert-adgroup conn adgroup))))

(defn upsert-keywords [rel]
  (jdbc/with-db-connection [conn (:db-spec config)]
    (doseq [keyword rel]
      (timbre/info "upserting keyword")
      (upsert-keyword conn keyword))))

(defn upsert-ads [rel]
  (jdbc/with-db-connection [conn (:db-spec config)]
    (doseq [ad rel]
      (timbre/info "upserting ad")
      (upsert-ad conn ad))))
