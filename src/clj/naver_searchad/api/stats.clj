(ns naver-searchad.api.stats
  (:require [jsonista.core :as json]
            [clojure.spec.alpha :as s]
            [naver-searchad.api.request :as request]
            [naver-searchad.utils :as utils]))

(s/def ::field
  #{:impCnt :clkCnt :ctr :cpc :avgRnk :ccnt :salesAmt :recentAvgCpc :recentAvgRnk :drtCrto :convAmt :crto})

(s/def ::fields (s/coll-of ::field))

(s/def ::date-preset #{:lastmonth :last7days :today :lastquarter :lastweek :yesterday :last30days})

(s/def ::time-increment #{:allDays :1})

(s/def ::breakdown #{"dayw" "hh24" "pcMblTp" "regnNo"})

(s/def ::time-range (s/keys :req-un [::since ::until]))

(s/def ::id string?)

(s/def ::ids (s/coll-of ::id))

(s/def ::query-params
  (s/keys :req-un [(or ::id ::ids) ::fields (or ::time-range ::date-preset)]
          :opt-un [::time-increment ::breakdown]))

(def default-fields [:impCnt :clkCnt :ccnt :salesAmt :cpc :convAmt :crto :ctr])

(s/fdef get-by-id
  :args (s/cat :creds map? :query-params ::query-params))

(defn get-by-id
  [creds query-params]
  (let [updater (if (:time-range query-params)
                  #(update % :time-range json/write-value-as-string)
                  #(update % :date-preset name))
        params (-> query-params
                   (update :fields json/write-value-as-string)
                   updater
                   utils/->camel-keys)]
    (request/GET creds "/stats" {:query-params params})))

(defn get-by-ids
  [creds query-params]
  (let [updater (if (:time-range query-params)
                  #(update % :time-range json/write-value-as-string)
                  #(update % :date-preset name))
        params (-> query-params
                   (update :fields json/write-value-as-string)
                   updater
                   utils/->camel-keys)
        ids (:ids params)]
    (mapcat #(request/GET creds "/stats" {:query-params (assoc params :ids %)})
            (partition-all ids 100))))

(defn by-id
  [creds query-params]
  (-> (get-by-id creds query-params)
      :body
      :data))

(defn by-ids
  [creds query-params]
  (-> (get-by-ids creds query-params)
      :body
      :data))
