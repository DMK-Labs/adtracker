(ns trendtracker.api
  (:require [keechma.toolbox.ajax :as ajax]
            [promesa.core :as p]
            [trendtracker.utils :as u]
            [clojure.string :as string]))

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

(defn parse-date-range
  "`dates` are a vector pair of js/moments
  [moment moment] => {:low str :high str}"
  [dates]
  (->> dates
       (map u/fmt-dt)
       (zipmap [:low :high])))

(defn total-performance [jwt customer-id dates]
  (ajax/GET "/api/performance"
            (req-params :data (assoc (parse-date-range dates)
                                     :customer-id customer-id)
                        :jwt jwt)))

(defn total-perf
  [jwt client-id range]
  (-> [(total-performance jwt client-id (:curr range))
       (total-performance jwt client-id (:prev range))]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn campaign-performance [jwt customer-id dates id]
  (ajax/GET "/api/performance/campaign"
            (req-params :data (assoc (parse-date-range dates)
                                     :id id
                                     :customer-id customer-id)
                        :jwt jwt)))

(defn campaign-perf
  [jwt client-id id range]
  (-> [(campaign-performance jwt client-id (:curr range) id)
       (campaign-performance jwt client-id (:prev range) id)]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn campaign-type-performance [jwt customer-id type dates]
  (ajax/GET "/api/performance/type"
            (req-params :data (assoc (parse-date-range dates)
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

(defn dataloader-req [req-params]
  (let [headers (:headers req-params)
        url (:url req-params)
        params (dissoc req-params :url :headers)]
    (ajax/GET (str "/api" url)
              (assoc default-request-config
                     :params params
                     :headers headers))))
