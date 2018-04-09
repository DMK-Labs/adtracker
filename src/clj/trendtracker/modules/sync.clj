(ns trendtracker.modules.sync
  "Syncing to and from Naver Searchad mgmt system. Gets the latest ad"
  (:require [naver-searchad.api.reports.stat :as reports.stat]
            [naver-searchad.api.reports.master :as reports.master]
            [naver-searchad.api.reports.common :as reports.common]
            [trendtracker.boundary.ad-results :as ad-results]
            [trendtracker.config :refer [config creds]]
            [trendtracker.utils.dates :as utils.dates]
            [java-time :as time]
            [taoensso.timbre :as timbre]))


;;; -- Downloading Latest Stats -----

(defn download-stats
  "Requests creation of `report-type` for `date`. Once the report job is
   built, returns the data as a java.io.BufferedReader. The report is in tsv
   format.

   Cleans up after itself by deleting requested job."
  [{:keys [customer-id report-type yyyymmdd]}]
  (let [c     (creds customer-id)
        id    (-> (reports.stat/create! c {:reportTp report-type :statDt yyyymmdd})
                  :body :reportJobId)
        fetch (fn []
                (timbre/info "Checking job status..." id (Thread/sleep 500))
                (reports.stat/fetch-job c id))]
    (try
      (loop [job (fetch)]
        (case (:status job)
          ("REGIST" "RUNNING" "WAITING") (recur (fetch))
          ("BUILT" "NONE") (reports.common/download c job)
          (throw
           (Exception.
            (str "Requested job returned unexpected status of: " (:status job))))))
      (finally
        (timbre/info
         "Cleaning job"
         id
         (:status @(reports.stat/delete! c id)))))))

(defn days-since-last
  "For a given `customer-id` and `table`, check the persistent db for entry,
   and returns a list of "
  [{:keys [customer-id table]}]
  (let [last (or (:during (ad-results/last-recorded {:customer-id customer-id
                                                     :table       table}))
                 (utils.dates/ago (time/days 1)))]
    (->> last
         utils.dates/days-since
         (map utils.dates/yyyymmdd))))

(defn pull-stats
  "Downloads stats for recent days missing entries."
  [{:keys [customer-id report-type table]}]
  (map #(download-stats {:customer-id customer-id
                         :report-type report-type
                         :yyyymmdd    %})
       (days-since-last {:customer-id customer-id
                         :table       table})))

(defn pull-and-append
  "Updates stats to latest."
  [kind customer-id]
  (let [[report-type table] (case kind
                              :ad ["AD" "naver.effectiveness"]
                              :ad-conversion ["AD_CONVERSION" "naver.conversion"])
        pulled (pull-stats {:customer-id customer-id
                            :report-type report-type
                            :table       table})]
    (when (not-empty pulled)
      (map #(ad-results/append table %) pulled))))

(defn sync-stats [customer-id]
  (do
    (pull-and-append :ad customer-id)
    (pull-and-append :ad-conversion customer-id)))

(comment
 (byte-streams/to-string
  (download-stats {:customer-id 137307
                   :report-type "AD"
                   :yyyymmdd    "20180328"}))
 (pull-stats {:customer-id 777309
              :report-type "AD"
              :table       "naver.effectiveness"})
 (pull-and-append :ad-conversion 137307)
 (pull-and-append :ad 137307)
 (pull-and-append :ad-conversion 719425)
 (pull-and-append :ad 719425)
 (pull-and-append :ad-conversion 777309)
 (pull-and-append :ad 777309))




;;; --- Upserting Master Reports (campaign tree)

(defn download-master
  "Requests creation of `report-type` for `date`. Once the report job is
   built, returns the data as a java.io.BufferedReader. The report is in tsv
   format.

   Cleans up after itself by deleting requested job."
  [{:keys [customer-id report-type yyyymmdd]}]
  (let [c     (creds customer-id)
        id    (:id (reports.master/create! c {:item report-type :fromTime yyyymmdd}))
        fetch (fn []
                (timbre/info "Checking job status..." id)
                (Thread/sleep 250)
                (reports.master/fetch-job c id))]
    (try
      (loop [job (fetch)]
        (case (:status job)
          ("REGIST" "RUNNING" "WAITING") (recur (fetch))
          "BUILT" (reports.common/as-csv c job)
          "NONE" nil
          (throw
           (Exception.
            (str "Requested job returned unexpected status of: " (:status job))))))
      (finally
        (timbre/info "Cleaning up" (:status (reports.master/delete! c id)) id)))))

(defn masters [{:keys [customer-id]}]
  (into
   {}
   (map #(vector % (download-master {:customer-id customer-id
                                     :report-type %}))
        ["Campaign"
         "BusinessChannel"
         "ContentsAd"
         "Qi"
         "CampaignBudget"
         "Adgroup"
         "AdExtension"
         "Keyword"
         "Ad"
         "ShoppingProduct"
         "AdgroupBudget"])))

(comment
 (download-master {:customer-id 137307 :report-type "Campaign"})
 (masters {:customer-id 137307}))
