(ns naver-searchad.api.ad
  (:require [naver-searchad.api.request :as request]))


(defn get-by-adgroup-id [creds id]
  (request/GET creds "/ncc/ads" {:query-params {:nccAdgroupId id}}))
