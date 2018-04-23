(ns optimus.rules
  (:require [trendtracker.models.aggregate-stats :as aggregate-stats]
            [trendtracker.config :refer [config creds]]
            [naver-searchad.api.adkeyword :as keyword-api]
            [huri.core :as h]
            [clara.rules :refer :all]))

(defn perfs
  ;; TODO: Helper fn Should be factored out...
  "Returns recent perfs of CUSTOMER for recent DAYS."
  [customer days]
  (aggregate-stats/recent-keyword-performance
   (:db-spec config)
   {:customer customer :days days}))

;; Fact definitions
(defrecord KeywordPerformance
  [customer-id adgroup-id keyword-id ad-rank-sum impressions clicks conversions cost revenue device])

(defrecord OverpaidKeyword
  [keyword-id])

(defrecord Bid
  [customer-id adgroup-id keyword-id bid])

(defn round-bid [n]
  (int (* (quot n 10)
          10)))

;; Rules
(defrule overpaid-keyword
  "Keywords which are bidding at a higher price than they can be expected to
   earn."
  [KeywordPerformance
   (pos? clicks)
   (> (/ cost clicks) (/ revenue clicks))
   (= ?k keyword-id)]
  =>
  (insert! (->OverpaidKeyword ?k)))

(defrule make-bid-at-expected-return
  [KeywordPerformance
   (= ?c customer-id)
   (= ?a adgroup-id)
   (= ?k keyword-id)
   (= ?b (max (round-bid (/ revenue clicks)) 80))]
  [OverpaidKeyword (= ?k keyword-id)]
 =>
 (insert! (->Bid ?c ?a ?k ?b)))

(defquery bids [] [?bid <- Bid])
(defquery overpaid-keywords [] [?ok <- OverpaidKeyword])

;; Run
(comment
 (defonce data (map map->KeywordPerformance (perfs 137307 30)))

 (let [session (-> (mk-session) (insert-all data) (fire-rules))]
   (query session overpaid-keywords)))

;;; Filters
(defn filter-hopeless [kw-map]
  "Filter keywords which lost the customer money. The revenue per click is lower than
   the bid. Bid should be lowered."
  (->> kw-map
       (filter #(< (:click-revenue %) (:avg-cpc %)))
       (filter #(< 10 (:clicks %)))))

;;; Derivations
(defn recommend-bid [kw-map]
  (assert (some? (:click-revenue kw-map)))
  (let [rpc (:click-revenue kw-map)]
    (assoc kw-map
      :bid (if (zero? rpc) 80 (round-bid rpc)))))

;;; Stateful actions
(defn update-bid!
  "KWS should be a list of keyword-maps containing [:keyword-id, :adgroup-id,
   :bid] keys."
  [customer kw]
  (keyword-api/update-keyword-bid! (creds customer) kw))

(defn lowball-low-performers!
  "Selects keywords of past DAYS which performed the worst (worst mismatch
   between bid and click-revenue), and lowballs them."
  [customer days]
  (->> (perfs customer days)
       filter-hopeless
       (map recommend-bid)
       (map #(update-bid! customer %))))

(comment
 (lowball-low-performers! 137307 30)
 (float (#(/ (->> %
                  filter-hopeless
                  (h/sum :cost))
             (->> %
                  (h/sum :cost)))
         (perfs 137307 14)))
 (->> (perfs 137307 14)
      (h/where {:conversions [> 0]})
      (h/sum :cost)))
