(ns naver-searchad.api.adgroup
  (:require [naver-searchad.api.campaign :as campaign]
            [naver-searchad.api.request :as request]
            [manifold.deferred :as deferred]
            [huri.core :as h]))

(def ->campaign-type
  {1 "WEB_SITE"
   4 "BRAND_SEARCH"
   2 "SHOPPING"})

(defn get-by-campaign [creds campaign-id]
  (request/GET creds "/ncc/adgroups" {:query-params {:nccCampaignId campaign-id}}))

(defn get-by-id [creds ids]
  (:body (request/GET creds "/ncc/adgroups" {:query-params {:ids ids}})))

(defn all
  "Aggregation fn to return a flat vector of all AdGroups."
  [creds]
  (let [campaign-ids (map :nccCampaignId (campaign/all creds))
        xf (comp
            (map #(get-by-campaign creds %))
            (map :body))]
    (transduce xf concat campaign-ids)))

(defn all-active
  [creds]
  (h/where {:status "ELIGIBLE"} (all creds)))
