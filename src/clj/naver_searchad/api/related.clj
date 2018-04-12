(ns naver-searchad.api.related
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [naver-searchad.api.request :as request]
            [naver-searchad.utils :as utils]))

(s/def ::params
  (s/keys
    :req-un [::hint-keywords]
    :opt-un [::site-id ::biztp-id ::event ::month ::show-detail]))

(defn- comma-list [kws] (string/join "," kws))

(s/fdef get
  :args (s/cat :creds ::request/creds
               :params ::params))

(defn get [creds {:keys [hint-keywords month show-detail] :as params}]
  (request/GET creds "/keywordstool"
               {:query-params
                (utils/->camel-keys
                 (update params :hint-keywords comma-list))}))
