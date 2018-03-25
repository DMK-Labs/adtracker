(ns trendtracker.core
  (:require [optimus.mckp :as mckp]
            [trendtracker.config :refer [config]]
            [trendtracker.modules.landscape :as landscape]
            [trendtracker.models.optimize :as optimize]
            [trendtracker.models.estimates :as estimates]
            [trendtracker.utils :as u]
            [trendtracker.models.aggregate-stats :as aggregate]
            [trendtracker.modules.predictor :as predictor]
            [huri.core :as h]))

(defonce ca-df (atom #{}))

(comment
 (def add-capharm (future (landscape/add-portfolio-estimates ca-df 137307)))
 (map count [(landscape/added ca-df :mobile)
             (landscape/added ca-df :pc)])

 (def capharm-estimates (estimates/by-customer 137307))
 (def re-estimated (predictor/derive-estimated-conversions
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
    (u/sum :marginal-conversions @fitted)))
;; => 121908.58047430584


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
 (filter
  even?
  (range 1 10))

 (mckp/marginal-landscape
  :conversions
  (sort-by :bid (h/where {:keyword-id "nkw-a001-01-000001107232059"}
                         re-estimated))))
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

