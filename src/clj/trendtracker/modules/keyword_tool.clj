(ns trendtracker.modules.keyword-tool
  (:require [naver-searchad.api.estimate :as estimate]
            [naver-searchad.api.related :as related]
            [trendtracker.config :refer [config creds]]
            [clojure.set :as set]
            [clojure.walk :as w]
            [trendtracker.models.keywords :as keywords]
            [huri.core :as h]
            [trendtracker.models.campaigns :as campaigns]
            [com.rpl.specter :refer :all]))

(defn first-place [kws]
  (estimate/nth-place-stats (:naver-creds config) 1 (set kws)))

(defn nth-place [n kws]
  (estimate/nth-place-stats (:naver-creds config) n (set kws)))

(defn rel-kws [{:keys [keywords month show-detail] :as params}]
  (let [kw-partitions (partition-all 5 keywords)]
    (mapcat #(->> (assoc params :hint-keywords %)
                  (related/get (:naver-creds config))
                  :body
                  :keywordList)
            kw-partitions)))

(defn min-bid-stats [{:keys [device keywords]}]
  (->> keywords
       (estimate/monthly-min-bid (:naver-creds config) device)
       (h/derive-cols {:bid (case device
                              :pc :min-pc-bid
                              :mobile :min-mobile-bid)
                       :device (constantly device)
                       :keywordplus (constantly false)})
       (estimate/performance-bulk (:naver-creds config))))

(defn min-bid-stats-both-channels [keywords]
  (let [pc (min-bid-stats {:device :pc :keywords keywords})
        mob (min-bid-stats {:device :mobile :keywords keywords})]
    (setval [ALL MAP-KEYS (pred (complement #{:keyword :keywordplus :device})) NAME BEGINNING]
            (str "min-")
            (concat pc mob))))

(defn median-bid-stats [{:keys [device keywords]}]
  (->> (estimate/median-bid (:naver-creds config) :keyword
                            {:device device
                             :period "MONTH"
                             :items keywords})
       (h/derive-cols {:keywordplus (constantly false)})
       (estimate/performance-bulk (:naver-creds config))))

(defn median-bid-stats-both-channels [keywords]
  (let [pc (median-bid-stats {:device :pc :keywords keywords})
        mob (median-bid-stats {:device :mobile :keywords keywords})]
    (setval [ALL MAP-KEYS (pred (complement #{:keyword :keywordplus :device})) NAME BEGINNING]
            (str "median-")
            (concat pc mob))))

(comment
  (median-bid-stats {:device :pc :keywords ["P2P대출" "대출"]})
  (median-bid-stats-both-channels ["P2P대출" "대출"]))

(defn simple-process [keywords include-related?]
  (let [uc (map #(.toUpperCase %) keywords)
        kws (if include-related?
              (map :relKeyword (rel-kws {:keywords uc}))
              uc)]
    (set/join (set/join (set/join (set/join (set/join (set/join (first-place kws)
                                                                (nth-place 2 kws)
                                                                {:keyword :keyword :device :device})
                                                      (nth-place 3 kws)
                                                      {:keyword :keyword :device :device})
                                            (nth-place 4 kws)
                                            {:keyword :keyword :device :device})
                                  (nth-place 5 kws)
                                  {:keyword :keyword :device :device})
                        (median-bid-stats-both-channels kws))
              (min-bid-stats-both-channels kws))))

(comment
  (min-bid-stats-both-channels ["로션" "플라젠트라" "튼살크림"])

  (median-bid-stats {:device :pc :keywords ["대출" "신용"]})

  (simple-process ["p2p대출"] false)

  (first-place ["P2P대출"]))
