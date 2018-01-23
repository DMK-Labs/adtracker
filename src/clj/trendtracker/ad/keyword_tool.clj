(ns trendtracker.ad.keyword-tool
  (:require [naver-searchad.api.estimate :as estimate]
            [trendtracker.config :refer [config]]))

(defn first-place [kws]
  (estimate/nth-place-stats (:naver-creds config) 1 (set kws)))

(defn fifth-place [kws]
  (estimate/nth-place-stats (:naver-creds config) 5 (set kws)))
