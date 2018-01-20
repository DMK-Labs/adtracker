(ns trendtracker.api
  (:require [keechma.toolbox.ajax :as ajax]
            [promesa.core :as p]
            [trendtracker.utils :as u]
            [clojure.string :as string]))

(defn parse-date-range
  "`dates` are a vector pair of js/moments
  [moment moment] => {:low str :high str}"
  [dates]
  (->> dates
       (map u/fmt-dt)
       (zipmap [:low :high])))

(defn total-performance [dates]
  (ajax/GET "/api/performance"
            {:params (parse-date-range dates)}))

(defn campaign-performance [dates id]
  (ajax/GET "/api/performance/campaign"
            {:params (parse-date-range dates)
             :id id}))

(defn campaign-type-performance [dates type]
  (ajax/GET "/api/performance/type"
            {:params (parse-date-range dates)
             :type type}))

(defn first-place-stats [keywords]
  (ajax/POST "/api/keyword-tool"
             {:body keywords}))
