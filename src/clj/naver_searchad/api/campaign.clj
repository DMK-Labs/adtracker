(ns naver-searchad.api.campaign
  (:require [naver-searchad.api.request :as request]
            [huri.core :as h]))

(defn get-all
  [creds]
  (request/GET creds "/ncc/campaigns" {}))

(defn all
  [creds]
  (:body (get-all creds)))

(defn all-active [creds]
  (h/where {:status "ELIGIBLE"} (all creds)))

(def ->campaignTp-string
  {:powerlink "WEB_SITE"
   :brand "BRAND_SEARCH"
   :shopping "SHOPPING"})

(defn get-by-type
  [creds campaign-type]
  (request/GET creds "/ncc/campaigns"
               {:query-params {:campaignType (->campaignTp-string campaign-type)}}))
