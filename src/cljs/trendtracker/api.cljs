(ns trendtracker.api
  (:require [keechma.toolbox.ajax :as ajax]
            [promesa.core :as p]
            [ajax.core :as aj]
            [trendtracker.utils :as u]))

(defn session-logged-in? [session-id]
  (ajax/POST "http://trendtracker.co.kr/rest/sessionApi/getValidSessionBySessionIdRest"
           {:body session-id #_"DBF1D4F1F3E4167032C78776B1694D45"}))

(def default-request-config
  {:response-format :json
   :keywords? true
   :format :json})

(defn add-auth-header [req-params jwt]
  (if jwt
    (assoc-in req-params [:headers :authorization] (str "Bearer " jwt))
    req-params))

(defn add-params [req-params params]
  (if params
    (assoc req-params :params params)
    req-params))

(defn req-params [& {:keys [jwt data]}]
  (-> default-request-config
      (add-auth-header jwt)
      (add-params data)))

;; User Login

(defn login [user]
  (ajax/POST "/api/login"
             (req-params :data user)))

;; Performance query apis
(defn total-performance [jwt customer-id dates]
  (ajax/GET "/api/performance"
            (req-params :data (assoc (u/parse-date-range dates)
                                :customer-id customer-id)
                        :jwt jwt)))

(defn total-perf
  [jwt client-id range]
  (-> [(total-performance jwt client-id (:curr range))
       (total-performance jwt client-id (:prev range))]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn adgroup-performance [jwt customer-id dates id]
  (ajax/GET "/api/performance/adgroup"
            (req-params :data (assoc (u/parse-date-range dates)
                                :id id
                                :customer-id customer-id))))
(defn adgroup-perf
  [jwt client-id id range]
  (-> [(adgroup-performance jwt client-id (:curr range) id)
       (adgroup-performance jwt client-id (:prev range) id)]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn campaign-performance [jwt customer-id dates id]
  (ajax/GET "/api/performance/campaign"
            (req-params :data (assoc (u/parse-date-range dates)
                                :campaign-id id
                                :customer-id customer-id))))
(defn campaign-perf
  [jwt client-id id range]
  (-> [(campaign-performance jwt client-id (:curr range) id)
       (campaign-performance jwt client-id (:prev range) id)]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn campaign-type-performance [jwt customer-id type dates]
  (ajax/GET "/api/performance/type"
            (req-params :data (assoc (u/parse-date-range dates)
                                :type type
                                :customer-id customer-id)
                        :jwt jwt)))

(defn campaign-type-perf
  [jwt client-id type range]
  (-> [(campaign-type-performance jwt client-id type (:curr range))
       (campaign-type-performance jwt client-id type (:prev range))]
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


