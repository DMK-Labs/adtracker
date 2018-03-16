(ns trendtracker.core
  (:require [optimus.mckp :as mckp]
            [trendtracker.config :refer [config]]
            [trendtracker.modules.landscape :as landscape]
            [trendtracker.models.optimize :as optimize]
            [trendtracker.models.estimates :as estimates]
            [trendtracker.utils :as u]
            [trendtracker.models.aggregate-stats :as aggregate]
            [optimus.conv-hull :as conv-hull]
            [huri.core :as h]))

(defonce ca-df (atom #{}))

(defn derive-estimated-conversions
  "Given a DF where each entry is a map including :key, :device, & estimated stats:

  1. Finds historical conversion data with aggregate/kwid->funnel for a
  customer-id, as a map of keyword-ids to funnel-maps

  2. For every keyword-id existing in DF, checks if historical conversion data
  exists: if it exists, naively assume CTR and CVR will be equal to historical
  data. If it does not exist, lowball estimates to 0.02%."
  [df]
  (let [kw->funnel (aggregate/kwid->funnel 137307)]
    (transduce
     (comp
      (map #(let [{ctr :ctr
                   cvr :cvr
                   :or {ctr 0.0002
                        cvr 0.01}}
                  (get kw->funnel (:key %))]
              (assoc %
                :ctr (double ctr)
                :cvr (double cvr)
                :clicks (double (* ctr (:impressions %)))
                :cost (double (* (:bid %) (* ctr (:impressions %))))
                :conversions (double (* cvr (* ctr (:impressions %))))))))
     conj
     df)))

(comment
  (def add-capharm (future (landscape/add-portfolio-estimates ca-df 137307)))
  (map count [(landscape/added ca-df :mobile)
              (landscape/added ca-df :pc)])
  ;; (optimize/insert-estimates 137307 @ca-df)
  (optimize/insert-click-marginals 137307 ca-marginals))

(comment
  (def capharm-estimates (estimates/by-customer 137307))
  (def re-estimated (derive-estimated-conversions capharm-estimates))

  (defonce ca-marginals
    (mckp/marginal-landscape
     :conversions
     (take 3000 re-estimated)))

  (map #(aggregate/kwid-ctr-cvr (:db-spec config)
                                {:id %})
       (map :key (take 10 @ca-df)))

  (let [fitted (mckp/fit-to-budget 1000000 ca-marginals)]
    (/ (u/sum :marginal-cost fitted)
       (u/sum :marginal-clicks fitted))))
