(ns naver-searchad.api.links
  (:require [naver-searchad.api.request :as request]))

(defn clients [creds]
  (request/GET creds "/customer-links" {:query-params {:type "MYCLIENTS"}}))

(defn managers [creds]
  (request/GET creds "/customer-links" {:query-params {:type "MYMANAGERS"}}))
