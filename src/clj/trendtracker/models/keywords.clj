(ns trendtracker.models.keywords
  (:require [hugsql.core :as hugsql]
            [naver-searchad.api.adkeyword :as adkeyword]
            [trendtracker.config :refer [config creds]]
            [trendtracker.models.campaigns :as campaigns]
            [huri.core :as h]
            [trendtracker.models.adgroups :as adgroups]
            [naver-searchad.api.adgroup :as adgroup]))

(def db-fns (hugsql/map-of-db-fns "sql/keywords.sql"))

(hugsql/def-db-fns "sql/keywords.sql")

(defn eligible-dep
  "Returns a list of the keywords {:keys [nccKeywordId ...]} under
   consideration for optimization."
  [customer-id]
  (h/where {:nccCampaignId (set (keys campaigns/target-device))
            ;; :status "ELIGIBLE"
            }
           (adkeyword/all (creds customer-id))))

(defn eligible
  "Improved version of `eligible` which, rather than find all adkeywords and
   filter out the ones eligible for optimization, works top to bottom,
   selecting the eligible adgroups first and then finding the keywords."
  [customer-id]
  (let [c (creds customer-id)
        grps (h/where {:nccCampaignId (set (keys campaigns/target-device))}
                      (adgroup/all c))]
    (transduce (comp
                (map #(adkeyword/get-by-adgroup-id c %))
                (map :body))
               concat
               (h/col :nccAdgroupId grps))))

(defn parent [keyword-id]
  (:adgroup-id (-parent (:db-spec config) {:id keyword-id})))

(defn target-device [keyword-id]
  (-> keyword-id
      parent
      adgroups/target-device))

(defn owner [keyword-id]
  (:customer-id (-owner (:db-spec config) {:id keyword-id})))

(comment
  (all (:db-spec config) {:customer-id 137307})
  (time (-owner (:db-spec config) {:id "nkw-a001-01-000001080813650"}))
  (set (map :campaign-id (eligible 137307)))
  (time (target-device "nkw-a001-01-000001080813650"))
  ;; :mobile
  (count (adkeyword/all (creds 137307))))



