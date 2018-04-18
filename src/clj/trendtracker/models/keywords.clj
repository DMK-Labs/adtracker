(ns trendtracker.models.keywords
  (:require [hugsql.core :as hugsql]
            [naver-searchad.api.adkeyword :as adkeyword]
            [trendtracker.config :refer [config creds]]
            [trendtracker.models.campaigns :as campaigns]
            [huri.core :as h]
            [naver-searchad.api.adgroup :as adgroup]))

(hugsql/def-db-fns "sql/keywords.sql")

(defn eligible-dep
  "Returns a list of the keywords {:keys [nccKeywordId ...]} under
   consideration for optimization."
  [customer-id]
  (h/where {:nccCampaignId (set (keys campaigns/target-device))}
           ;; :status "ELIGIBLE"

           (adkeyword/get-all (creds customer-id))))

(defn eligible
  "Improved version of `eligible` which, rather than find all adkeywords and
   filter out the ones eligible for optimization, works top to bottom,
   selecting the eligible adgroups first and then finding the keywords."
  [customer-id]
  (let [c (creds customer-id)
        grps (h/where {:nccCampaignId (set (keys campaigns/target-device))}
                      (adgroup/all c))]
    (map #(adkeyword/get-by-adgroup c %)
         (h/col :nccAdgroupIdgrps))))
