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
  "Given a DF where each entry is a map including :key, :device, & estimated
  stats:

  1. Finds historical conversion data with aggregate/kwid->funnel for a
  customer-id, as a map of keyword-ids to funnel-maps

  2. For every keyword-id existing in DF, checks if historical conversion data
  exists: if it exists, naively assume CTR and CVR will be equal to historical
  data. If it does not exist, lowball estimates (to 0.1%).

  3. For new-ish keyword-ids, there may be no historical data, but historical
  data could exist for equivalent keywords in the past."
  [df]
  (let [kwid->funnel (aggregate/kwid->funnel 137307)
        kw->funnel (aggregate/kw->funnel 137307)]
    (transduce
     (map #(let [{imp :impressions bid :bid} %
                 {ctr :ctr cvr :cvr :or {ctr 0.001
                                         cvr 0.001}}
                 (get kwid->funnel (:key %))]
             (assoc %
                    :ctr ctr
                    :cvr cvr
                    :clicks (* ctr imp)
                    :cost (* bid (* ctr imp))
                    :conversions (* cvr (* ctr imp)))))
     conj
     df)))

(comment
  (def add-capharm (future (landscape/add-portfolio-estimates ca-df 137307)))
  (map count [(landscape/added ca-df :mobile)
              (landscape/added ca-df :pc)])

  (def capharm-estimates (estimates/by-customer 137307))
  (def re-estimated (derive-estimated-conversions
                     (h/where {:bid [< 1500]} capharm-estimates)))
  (def ca-marginals (mckp/marginal-landscape
                     :conversions
                     re-estimated))

  (optimize/insert-click-marginals 137307 ca-marginals)
  
  (def fitted (future (mckp/fit-to-budget 1724100 ca-marginals)))
  (realized? fitted)
  ;; => false
  (def bids (mckp/bid-vectors fitted))
  (/ (u/sum :marginal-cost @fitted)
     (u/sum :marginal-clicks @fitted))
  ;; => 1219.0858047428715
  (/ (u/sum :marginal-cost @fitted)
     (u/sum :marginal-conversions @fitted))
  ;; => 121908.58047430584
  )

(comment
  (def kwid->funnel (aggregate/kwid->funnel 137307))
  (def kw->funnel (aggregate/kw->funnel 137307))
  
  (first kwid->funnel)
  ;; =>
  ;; ["nkw-a001-01-000001088394951"
  ;;  {:keyword_id "nkw-a001-01-000001088394951",
  ;;   :keyword "유아보습로션",
  ;;   :impressions 52,
  ;;   :clicks 0,
  ;;   :conversions 0,
  ;;   :ctr 0.0,
  ;;   :cvr 0.0,
  ;;   :i2c 0.0}]
  
  (first kw->funnel)
  ;; =>
  ;; ["폼클렌저"
  ;;  {:keyword "폼클렌저",
  ;;   :impressions 17,
  ;;   :clicks 0,
  ;;   :conversions 0,
  ;;   :ctr 0.0,
  ;;   :cvr 0.0,
  ;;   :i2c 0.0}]
  
  (mckp/marginal-landscape
   :conversions
   (sort-by :bid (h/where {:keyword-id "nkw-a001-01-000001107232059"}
                          re-estimated)))
  
  ;; =>
  ;; {:impressions 775,
  ;;  :clicks 0.155,
  ;;  :keyword-id "nkw-a001-01-000001107232059",
  ;;  :conversions 1.55E-4,
  ;;  :device "PC",
  ;;  :cost 79.05,
  ;;  :keywordplus false,
  ;;  :cvr 0.001,
  ;;  :ctr 2.0E-4,
  ;;  :bid 510}
  )
