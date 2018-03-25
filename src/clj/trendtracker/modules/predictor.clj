(ns trendtracker.modules.predictor
  (:require [trendtracker.models.aggregate-stats :as aggregate]))

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
        kw->funnel   (aggregate/kw->funnel 137307)]
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
