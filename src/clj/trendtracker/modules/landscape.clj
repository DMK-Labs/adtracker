(ns trendtracker.modules.landscape
  (:require [clojure.set :as set]
            [huri.core :as h]
            [naver-searchad.api.estimate :as estimate]
            [optimus.mckp :as mckp]
            [trendtracker.config :refer [config creds]]
            [trendtracker.models.keywords :as keywords]
            [trendtracker.models.campaigns :as campaigns]
            [trendtracker.modules.keyword-tool :as keyword-tool]
            [trendtracker.models.optimize :as optimize]))

(defn- perfs [creds type item]
  (println "fetching perfs..." (select-keys item [:device :key]))
  (estimate/all-performances creds type item))

(defn added
  "Returns a list of the keyword-ids which have been added to the dataframe
   atom."
  [df device]
  (->> @df
       (h/where {:device (.toUpperCase (name device))})
       (h/col :key)
       set))

(defn add-perfs [creds df device kws]
  (let [by-device (group-by #(campaigns/target-device (:nccCampaignId %)) kws)
        device-subset (->> by-device
                           device
                           (map :nccKeywordId)
                           set)
        remaining (set/difference device-subset (added df device))
        device-string (.toUpperCase (name device))]
    (if (empty? remaining)
      (println "No more keywords to add!")
      (doseq [k remaining]
        (swap! df
               set/union
               (set (perfs creds :id {:device device-string :keywordplus false :key k})))))
    (println "done!")))

(defn add-portfolio-estimates [df customer-id]
  (let [creds (creds customer-id)
        kws (keywords/eligible customer-id)]
    (add-perfs creds df :mobile kws)
    (add-perfs creds df :pc kws)))

(defn ridgeline [marginals]
  (->> marginals
       (sort-by :marginal-efficiency >)
       (reductions
        (fn [{:keys [clicks cost impressions]}
             {:keys [marginal-clicks marginal-cost marginal-impressions]}]
          (let [c (+ clicks marginal-clicks)
                cst (+ cost marginal-cost)
                i (+ impressions marginal-impressions)]
            {:clicks c
             :cost cst
             :impressions i
             :cpc (if (zero? c) 0 (/ cst c))
             :ctr (if (zero? i) 0 (/ c i))}))
        {:clicks 0
         :cost 0
         :impressions 0
         :cpc 0
         :ctr 0})
       (partition-by :clicks)
       (map first)))

(defn stats [df cost]
  (let [res (mckp/fit-to-budget cost df)
        clicks (h/sum :marginal-clicks res)
        cost (h/sum :marginal-cost res)
        impressions (h/sum :marginal-impressions res)]
    {:clicks clicks
     :cost cost
     :impressions impressions
     :cpc (double (/ cost clicks))
     :cpm (* 1000 (/ cost impressions))}))

(defn final-bid-perf-estimates
  "From a seq of maps representing marginal bids and their associated performance
  deltas, returns a list of each key and its final (max) bid."
  [marginals]
  (let [grouper (if (:key (first marginals))
                  :key
                  :keyword-id)]
    (->> (group-by grouper marginals)
         vals
         (map #(apply max-key :bid %)))))

(defn detail [df cost]
  (final-bid-perf-estimates (mckp/fit-to-budget cost df)))

(defn optimized-bids-with-min-70 [customer-id budget]
  (->> [(detail (optimize/fetch-marginals customer-id) budget)
        (map #(-> %
                  (select-keys [:nccKeywordId :keyword :nccAdgroupId])
                  (set/rename-keys {:nccKeywordId :keyword-id
                                    :nccAdgroupId :adgroup-id})
                  (assoc :bid 70))
             (keywords/eligible customer-id))]
       (map (partial group-by :keyword-id))
       (apply merge-with concat)
       vals
       (map (partial apply merge-with (fn [a b]
                                        (if (integer? a)
                                          (max a b)
                                          b))))))

;; while min-d
(def optimized-bids-with-minimums optimized-bids-with-min-70)
;; (defn optimized-bids-with-minimums [customer-id budget]
;;   (->> [(detail (optimize/fetch-marginals customer-id) budget)
;;         (keyword-tool/min-bid (keywords/eligible customer-id))]
;;        (map (partial group-by :keyword-id))
;;        (apply merge-with concat)
;;        vals
;;        (map (partial apply merge-with (fn [a b]
;;                                         (if (integer? a)
;;                                           (max a b)
;;                                           b))))))

(comment
  (set (h/col :status (keywords/eligible 1334028)))
  ;; => #{"ELIGIBLE"}
  (count (keywords/eligible 1334028))

  (count (optimize/fetch-marginals 1334028))

  (keyword-tool/min-bid (keywords/eligible)))
