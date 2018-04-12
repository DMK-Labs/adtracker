(ns naver-searchad.api.estimate
  (:require [clojure.spec.alpha :as s]
            [huri.core :as h]
            [naver-searchad.api.request :as request]
            [clojure.set :as set]
            [semantic-csv.core :as scsv]
            [com.rpl.specter :refer :all]))

(s/def ::tp #{:id :keyword})
(s/def ::bid #(<= 70 % 100000))
(s/def ::bids (s/and #(<= (count %) 100)
                     (s/coll-of ::bid)))
(s/def ::perf-req-body (s/keys :req-un [::device ::keywordplus ::key ::bids]))

(s/fdef get-performance
  :args (s/cat :creds ::request/creds
               :tp ::tp
               :body ::perf-req-body))

(defn get-performance [creds tp body]
  ((request/POST
    creds
    (str "/estimate/performance/" (name tp))
    {:body body})
   :body))

(def dedup-clk-imp
  (comp
   (partition-by (juxt :key :device :keywordplus :clicks :impressions))
   (map first)))

(def dedup-clk
  (comp
   (partition-by (juxt :key :device :keywordplus :clicks))
   (map first)))

(defn performance
  [creds tp {:keys [device keywordplus key bids] :as body}]
  (let [estimates (:estimate (get-performance creds tp body))]
    (transduce
     (comp (map #(merge (dissoc body :bids) %))
           dedup-clk-imp)
     conj
     estimates)))

(defn max-performance
  [creds tp {:keys [device keywordplus key] :as body}]
  (first (performance creds tp
                      (assoc body :bids [30000]))))

(defn all-performances
  "Checks for the max performance, and if it is non-zero, calls `performance`
  sequentially until the maximum is found. Returns a vector of all relevant
  performance results."
  [creds tp {:keys [device keywordplus key] :as body}]
  (let [bid-groupings (partition-all 100 (range 70 30010 10))
        max-perf (-> (max-performance creds tp body)
                     (select-keys [:clicks :impressions]))]
    (loop [bid-groups bid-groupings
           result []]
      (let [perfs (->> (assoc body :bids (first bid-groups))
                       (performance creds tp))]
        (if (->> (h/select-cols [:clicks :impressions] perfs)
                 (some #(= max-perf %)))
          (concat result perfs)
          (recur (next bid-groups) (concat result perfs)))))))

(defn average-position-bid
  ":items are vectors of key and position:
  
  {:key \"foo\" :position \"1\"}"
  [creds tp {:keys [device items] :as body}]
  (let [items (map #(update % :position str) items)
        uri (str "/estimate/average-position-bid/" (name tp))
        parts (partition-all 200 items)]
    (mapcat
     (fn [items]
       (->> (request/POST creds uri {:body (assoc body :items items)})
            :body
            :estimate
            (map #(assoc % :device device))))
     parts)))

(defn all-position-bids
  "Given just the keyword (or id), returns the avg position bid for every powerlink
  slot: PC 1~15, MOBILE 1~5."
  [creds tp keyword]
  (let [->item (fn [pos] {:key keyword :position pos})
        pc-req {:device "PC" :items (map ->item (range 1 16))}
        mobile-req {:device "MOBILE" :items (map ->item (range 1 6))}]
    (mapcat #(average-position-bid creds tp %) [pc-req mobile-req])))

(defn post-performance-bulk
  [creds items]
  (->> (request/POST creds "/estimate/performance-bulk"
                     {:body {:items items}})
       :body
       :items))

(defn performance-bulk
  "item: {:device :keywordplus :keyword :bid}

  Naver API handles 200 ITEMS at a time, this seamlessly handles item lists
  longer than 200 by partitioning."
  [creds items]
  (let [item-partitions (partition-all 200 items)]
    (mapcat #(post-performance-bulk creds %) item-partitions)))

(defn post-exposure-minimum-bid
  [creds tp {:keys [device period items] :as body}]
  (->> (request/POST creds
                     (str "/estimate/exposure-minimum-bid/" (name tp))
                     {:body body})
       :body
       :estimate
       (map #(assoc % :device device :period period))))

(defn exposure-minimum-bid
  "items: [:keyword]

  Naver API handles 200 ITEMS at a time, this seamlessly handles item lists
  longer than 200 by partitioning."
  [creds tp {:keys [device period items] :as body}]
  (let [partitions (partition-all 200 items)]
    (mapcat #(post-exposure-minimum-bid creds tp (assoc body :items %))
            partitions)))

(defn monthly-min-bid [creds device kws]
  (let [new-key  (keyword (str "min-" (name device) "-bid"))
        groups   (partition-all 100 kws)
        min-bids (mapcat
                  (fn [kws] (exposure-minimum-bid creds :keyword {:device (.toUpperCase (name device))
                                                                  :period "MONTH"
                                                                  :items  kws}))
                  groups)
        renamed  (set/rename min-bids {:bid new-key})]
    (map #(select-keys % [:keyword new-key]) renamed)))

(defn avg-first-bid [creds device kws]
  (let [new-key (keyword (str "fp-" (name device) "-bid"))
        groups  (partition-all 100 kws)
        fp-bids (mapcat
                 (fn [kws] (average-position-bid
                            creds
                            :keyword
                            {:device device
                             :items  (map
                                      (fn [k v] {:key k :position v})
                                      kws
                                      (repeat 1))}))
                 groups)
        renamed (set/rename fp-bids {:bid new-key})]
    (map #(select-keys % [:keyword new-key]) renamed)))

(defn post-median-bid
  [creds tp {:keys [device period items] :as body}]
  (->> (request/POST creds
                     (str "/estimate/median-bid/" (name tp))
                     {:body body})
       :body
       :estimate
       (map #(assoc % :device device :period period))))

(defn median-bid
  "Naver API handles 200 ITEMS at a time, this seamlessly handles item lists
  longer than 200 by partitioning."
  [creds tp {:keys [device period items] :as body}]
  (let [partitions (partition-all 200 items)]
    (mapcat #(post-median-bid creds tp (assoc body :items %))
            partitions)))

(defn nth-place-stats [creds n keywords]
  (let [pc-bids (average-position-bid
                 creds
                 :keyword {:device "PC"
                           :items (map
                                   (fn [kw] {:key kw :position n})
                                   keywords)})
        mob-bids (average-position-bid
                  creds
                  :keyword {:device "MOBILE"
                            :items (map
                                    (fn [kw] {:key kw :position n})
                                    keywords)})]
    (setval [ALL MAP-KEYS (pred (complement #{:keyword :keywordplus :device})) NAME BEGINNING]
            (str n "-")
            (concat
             (performance-bulk creds (map #(assoc % :keywordplus false) pc-bids))
             (performance-bulk creds (map #(assoc % :keywordplus false) mob-bids))))))
