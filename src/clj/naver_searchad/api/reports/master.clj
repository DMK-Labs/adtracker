(ns naver-searchad.api.reports.master
  (:require [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [huri.core :as h]
            [java-time :as time]
            [naver-searchad.api.reports.common :as reports.common]
            [naver-searchad.api.request :as request]
            [naver-searchad.date-utils :as date-utils]
            [naver-searchad.api.campaign :as campaign]))

(def master-reports
  "17 in total, 16 usually for a client because the Account report is
  unavailable."
  #{"Campaign" "CampaignBudget" "BusinessChannel" "Adgroup"
    "AdgroupBudget" "Keyword" "Account" "Ad" "AdExtension"
    "Qi" "Label" "LabelRef" "Media" "Biz" "SeasonalEvent"
    "ShoppingProduct" "ContentsAd"})

;;* Fetch and download

(defn all-jobs
  [creds]
  (:body
   (request/GET creds "/master-reports")))

(defn fetch-job
  [creds id]
  (:body (request/GET creds (str "/master-reports/" id))))

(defn fetch-jobs
  [creds ids]
  (doall (map #(fetch-job creds %) ids)))

;;* Create and delete

(s/fdef create!
  :args (s/cat :creds ::request/creds
               :report-type master-reports
               :since ::date-utils/iso-8601))

(defn create!
  "Returns the report job info map."
  [creds {:keys [item fromTime] :as body}]
  (:body
   (request/POST creds "/master-reports" {:body body})))

(defn create-master-reports!
  "Creds are the credentials of the client. May create some reports with no info.
  Does not create the \"Account\" report by default."
  ([creds] (create-master-reports! creds nil))
  ([creds fromTime]
   (map #(create! creds {:item %
                         :fromTime fromTime})
        (set/difference master-reports #{"Account"}))))

(defn delete!
  [creds id]
  @(request/raw creds :delete (str "/master-reports/" id) {}))

(defn delete-by-user!
  "Delete all report jobs belonging to user with LOGIN-ID."
  [creds login-id]
  (map #(delete! creds %)
       (->> (all-jobs creds)
            (h/where {:managerLoginId login-id})
            (map :id))))

;;* Fetch

(defn all-my-jobs
  [{:keys [creds manager] :as conf}]
  (h/where {:managerLoginId manager}
           (all-jobs creds)))

(defn fetch-reports
  [{:keys [creds manager] :as conf} jobs]
  (mapv #(reports.common/as-edn creds %) jobs))
