(ns naver-searchad.keyword-tool
  (:require [clojure.set :as set]
            [naver-searchad.trend :as trend]
            [naver-searchad.api.estimate :as estimate]))

;; (def creds (:creds env))

(defn max-perf [creds device kws]
  (set/rename
   (let [groups (partition-all 100 kws)]
     (mapcat
      (fn [kws] (->> kws
                     (map #(estimate/all-performances
                            creds
                            :keyword {:device device :keywordplus true :key %}))
                     (remove nil?)
                     (map #(apply max-key :bid %))))
      groups))
   {:key         :keyword
    :bid         (keyword (str "max-" (name device) "-bid"))
    :cost        (keyword (str "max-" (name device) "-cost"))
    :impressions (keyword (str "max-" (name device) "-impressions"))
    :clicks      (keyword (str "max-" (name device) "-clicks"))}))

(defn stats [creds kws]
  (let [rel      (set/rename
                  (trend/frends kws)
                  {:relKeyword :keyword})
        all-kws  (map :keyword rel)
        min-bids (set/join
                  (estimate/monthly-min-bid creds :pc all-kws)
                  (estimate/monthly-min-bid creds :mobile all-kws))
        fp-bids  (set/join
                  (estimate/avg-first-bid creds :pc all-kws)
                  (estimate/avg-first-bid creds :mobile all-kws)
                  {:keyword :keyword})
        ;; maxes (map #(dissoc % :device)
        ;;           (set/join (max-perf :pc all-kws)
        ;;                     (max-perf :mobile all-kws)
        ;;                     {:keyword :keyword}))
        final    (set/join
                  (set/join
                   rel
                   fp-bids
                   {:keyword :keyword})
                  min-bids
                  {:keyword :keyword})]
    final))

#_(bids ["블랙박스" "지넷시스템"])
