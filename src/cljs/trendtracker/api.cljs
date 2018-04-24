(ns trendtracker.api
  (:require [keechma.toolbox.ajax :as ajax]
            [promesa.core :as p]
            [ajax.core :as aj]
            [trendtracker.utils :as u]))

(defn session-logged-in?
  "Asks REST endpoint on TrendTracker server for confirmation of session
   validity. Returns true or false."
  [session-id]
  (ajax/POST "http://trendtracker.co.kr/rest/sessionApi/getValidSessionBySessionIdRest"
             {:body session-id}))

(def default-request-config
  {:response-format :json
   :keywords? true
   :format :json})

(defn add-params [req-params params]
  (if params
    (assoc req-params :params params)
    req-params))

(defn req-params [& {:keys [data]}]
  (-> default-request-config
      (add-params data)))


;; User Login

(defn login [user]
  (ajax/POST "/api/login"
             (req-params :data user)))

;; Performance query apis
(defn total-performance [customer-id dates]
  (ajax/GET "/api/performance"
            (req-params :data (assoc (u/parse-date-range dates)
                                :customer-id customer-id))))

(defn total-perf
  [client-id range]
  (-> [(total-performance client-id (:curr range))
       (total-performance client-id (:prev range))]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn adgroup-performance [customer-id dates id]
  (ajax/GET "/api/performance/adgroup"
            (req-params :data (assoc (u/parse-date-range dates)
                                :id id
                                :customer-id customer-id))))
(defn adgroup-perf
  [client-id id range]
  (-> [(adgroup-performance client-id (:curr range) id)
       (adgroup-performance client-id (:prev range) id)]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn campaign-performance [customer-id dates id]
  (ajax/GET "/api/performance/campaign"
            (req-params :data (assoc (u/parse-date-range dates)
                                :campaign-id id
                                :customer-id customer-id))))
(defn campaign-perf
  [client-id id range]
  (-> [(campaign-performance client-id (:curr range) id)
       (campaign-performance client-id (:prev range) id)]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn campaign-type-performance [customer-id type dates]
  (ajax/GET "/api/performance/type"
            (req-params :data (assoc (u/parse-date-range dates)
                                :type type
                                :customer-id customer-id))))

(defn campaign-type-perf
  [client-id type range]
  (-> [(campaign-type-performance client-id type (:curr range))
       (campaign-type-performance client-id type (:prev range))]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn keyword-stats [data]
  (ajax/POST "/api/keyword-tool"
             (req-params :data data)))

(defn optimize-settings [customer-id]
  (ajax/GET "/api/optimize/settings"
            (req-params :data {:customer-id customer-id})))

(defn save-settings [settings]
  (ajax/PUT "/api/optimize/settings"
            (req-params :data settings)))
;; {:customer-id 777309
;;  :budget 99
;;  :objective :clicks
;;  :targets (js/JSON.stringify (clj->js ["test" "foo"]))}


(defn dataloader-req [req-params]
  (let [headers (:headers req-params)
        url (:url req-params)
        params (dissoc req-params :url :headers)]
    (ajax/GET (str "/api" url)
              (assoc default-request-config
                :params params
                :headers headers))))


