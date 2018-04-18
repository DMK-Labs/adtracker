(ns trendtracker.modules.sync
  "Syncing to and from Naver Searchad mgmt system. Gets the latest ad"
  (:require [naver-searchad.api.reports.stat :as reports.stat]
            [naver-searchad.api.reports.master :as reports.master]
            [naver-searchad.api.reports.common :as reports.common]
            [trendtracker.boundary.ad-results :as ad-results]
            [trendtracker.config :refer [config creds]]
            [trendtracker.utils.dates :as utils.dates]
            [java-time :as time]
            [taoensso.timbre :as timbre]
            [trendtracker.models.master-reports :as master-reports]))


;;; -- Downloading Latest Stats -----

(defn download-stats
  "Requests creation of `report-type` for `date`. Once the report job is
   built, returns the data as a java.io.BufferedReader. The report is in tsv
   format.

   Cleans up after itself by deleting requested job."
  [{:keys [customer-id report-type yyyymmdd]}]
  (let [c (creds customer-id)
        id (-> (reports.stat/create! c {:reportTp report-type :statDt yyyymmdd})
               :body :reportJobId)
        fetch (fn []
                (timbre/info "Checking job status..." id (Thread/sleep 500))
                (reports.stat/fetch-job c id))]
    (try
      (loop [job (fetch)]
        (timbre/info "Job status:" (:status job))
        (case (:status job)
          ("REGIST" "RUNNING" "WAITING") (recur (fetch))
          "BUILT" (reports.common/download c job)
          "NONE" nil
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
  (let [last (or (:during
                  (ad-results/last-recorded {:customer-id customer-id :table table}))
                 (utils.dates/ago (time/days 1)))]
    (->> last
         utils.dates/days-since
         (map utils.dates/yyyymmdd))))

(defn pull-stats
  "Downloads stats for recent days missing entries."
  [{:keys [customer-id report-type table]}]
  (keep #(download-stats {:customer-id customer-id
                          :report-type report-type
                          :yyyymmdd %})
        (days-since-last {:customer-id customer-id
                          :table table})))

(defn pull-and-append
  "Updates stats to latest."
  [customer-id kind]
  (let [[report-type table] (case kind
                              :ad ["AD" "naver.effectiveness"]
                              :ad-conversion ["AD_CONVERSION" "naver.conversion"])
        pulled (pull-stats {:customer-id customer-id
                            :report-type report-type
                            :table table})]
    (when (not-empty pulled)
      (map #(ad-results/append table %) pulled))))

(comment
 (byte-streams/to-string
  (download-stats {:customer-id 719425
                   :report-type "AD"
                   :yyyymmdd "20180403"}))
 (days-since-last {:customer-id 719425 :table "naver.effectiveness"})
 (pull-stats {:customer-id 719425
              :report-type "AD"
              :table "naver.effectiveness"})
 (pull-and-append 137307 :ad-conversion)
 (pull-and-append 137307 :ad)
 (pull-and-append 719425 :ad-conversion)
 (pull-and-append 719425 :ad)
 (pull-and-append 777309 :ad-conversion)
 (pull-and-append 777309 :ad))




;;; --- Upserting Master Reports (campaign tree)

(defn download-master
  "Requests creation of `report-type` for `date`. Once the report job is
   built, returns the data as a java.io.BufferedReader. The report is in tsv
   format.

   Cleans up after itself by deleting requested job."
  [{:keys [customer-id report-type yyyymmdd]}]
  (let [c (creds customer-id)
        id (:id (reports.master/create! c {:item report-type :fromTime yyyymmdd}))
        fetch (fn []
                (timbre/info "Checking job status..." id)
                (Thread/sleep 250)
                (reports.master/fetch-job c id))]
    (try
      (loop [job (fetch)]
        (timbre/info "Job status:" (:status job))
        (case (:status job)
          ("REGIST" "RUNNING" "WAITING") (recur (fetch))
          ("BUILT" "NONE") (reports.common/as-edn c job)
          (throw
           (Exception.
            (str "Requested job returned unexpected status of: " (:status job))))))
      (finally
        (timbre/info "Cleaning up" (:status (reports.master/delete! c id)) id)))))

(defn masters [customer-id reports]
  (->> (if (= :all reports)
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
          "AdgroupBudget"]
         reports)
       (map #(vector % (download-master {:customer-id customer-id :report-type %})))
       (into {})))

(def master-upserters
  {"Campaign" master-reports/upsert-campaigns
   "BusinessChannel" master-reports/upsert-business-channels
   "Adgroup" master-reports/upsert-adgroups
   "Keyword" master-reports/upsert-keywords
   "Ad" master-reports/upsert-ads})

(defn sync-client-masters [client-id reports]
  (doseq [[report-type rel] (masters client-id reports)]
    (timbre/info "Upserting:" report-type)
    ((master-upserters report-type) rel)))

(comment
 (masters 137307 :all)
 (sync-client-masters 719425 ["Campaign" "BusinessChannel" "Adgroup"])
 (sync-client-masters 719425 ["Keyword" "Ad"]))
