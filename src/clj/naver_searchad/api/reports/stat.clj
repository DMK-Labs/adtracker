(ns naver-searchad.api.reports.stat
  (:require [camel-snake-kebab.core :as csk]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [huri.core :as h]
            [naver-searchad.api.reports.common :as reports.common]
            [naver-searchad.api.request :as request]
            [naver-searchad.date-utils :as date-utils]))

(def week-reports
  #{:ad-detail :ad-conversion-detail})

(def yr-reports
  #{:naverpay-conversion :ad-conversion :ad :expkeyword :adextension :adextension-conversion})

(def reports (set/union week-reports yr-reports))

;;* GETting and Fetching

(defn all-jobs
  "Returns a list of all report jobs."
  [creds]
  (:body (request/GET creds "/stat-reports")))

(defn all-my-jobs
  [{:keys [creds manager] :as conf}]
  (->> (all-jobs creds)
       (h/where {:loginId manager})))

(defn fetch-job
  "Fetches the report job info map."
  [creds id]
  (:body (request/GET creds (str "/stat-reports/" id))))

;;* Creating

(s/fdef create
  :args (s/cat :creds ::request/creds
               :report-type reports
               :date ::date-utils/date-string))

(defn create!
  "Requests the creation of a report on Naver's servers. Once finished, they can
  be downloaded and saved as tsv files with `download`, or fetched as data with
  `fetch-as-edn`.
  
  - Given no date, will create a report for yesterday.
  - Reports can be queried up to a year in the past.
  - AD_DETAIL and AD_CONVERSION_DETAIL may only be queried for the past 7 days.

  Additionally, the Naver system can only hold 100 of these Stat Report Jobs. Additional ones"
  [creds {:keys [reportTp statDt] :as body}]
  (request/POST creds "/stat-reports"
                {:body (update body :reportTp csk/->SCREAMING_SNAKE_CASE_STRING)}))


(defn create-recent-n!
  [{:keys [creds] :as conf} n report-type]
  (map #(create! creds {:reportTp report-type :statDt %})
       (date-utils/last-n-days n)))

;;* Deleting

(defn delete!
  [creds report-id]
  (request/raw creds :delete (str "/stat-reports/" report-id) {}))

(defn delete-by-user!
  "Delete all report jobs belonging to user with LOGIN-ID."
  [creds login-id]
  (let [jobs (all-jobs creds)
        job-ids (->> jobs
                        (h/where {:loginId login-id})
                        (map :reportJobId))]
    (doseq [job-id job-ids]
      (do (Thread/sleep 50)
          (delete! creds job-id)))))

;;* To be passed conf map

(defn fetch-reports
  "Mapcats results (i.e. should only be used on report jobs of the same type)"
  [{:keys [creds manager] :as conf} jobs]
  (mapcat #(reports.common/as-edn creds %) jobs))
