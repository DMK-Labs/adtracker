(ns optimus.mckp
  (:require [optimus.conv-hull :as conv-hull]
            [optimus.utils :as u]
            [clojure.spec.alpha :as s]
            [huri.core :as h]
            [orchestra.spec.test :as st]))

(s/def ::marginal-performance
  (s/keys :req-un [(or ::key ::keyword-id) ::keywordplus ::device ::bid
                   ::marginal-bid ::marginal-clicks ::marginal-cost ::marginal-impressions]))

(s/def ::marginal-performances
  (s/coll-of ::marginal-performance))

(defn efficiency
  "objective / cost

  A measure of how efficiently the objective is achieved.
  Avoids divide-by-zero problems by defaulting to 0.

  If no cost-fn is given, defaults to :marginal-cost"
  ([m] (efficiency :marginal-clicks m))
  ([objective m]
   (u/divide-unless-zero (objective m) (:marginal-cost m)))
  ([objective cost m]
   (u/divide-unless-zero (objective m) (cost m))))


(defn kp-marginals
  "Greedy knapsack problem solver. Given an marginal-objective, a list of PTS,
  and a target capacity.

  Sorts the list of PTS by decreasing efficiency (as per the
  MARGINAL-OBJECTIVE), then takes points until CAPACITY is reached."
  [marginal-objective marginal-cost capacity pts]
  (loop [coll (sort-by #(efficiency marginal-objective marginal-cost %) > pts)
         res []
         cost 0]
    (if (or (empty? coll)
            (> (+ cost (marginal-cost (first coll))) capacity))
      res
      (recur (next coll)
             (conj res (first coll))
             (+ cost (marginal-cost (first coll)))))))


(defn derive-margins
  "New implementation which also returns the first increment.

  Only wants a flat vector of maps belonging to the same class.

  Returns a vector of maps, where each map contains the original keys [:bid
  :clicks :cost :device :key], as well as new [:marginal-bid :marginal-clicks
  :marginal-cost :marginal-cpc] keys."
  [estimates]
  (assert (apply = (map u/keydev estimates)))
  (let [pts (->> estimates
                 (h/derive-cols {;; :marginal-revenue :revenue
                                 :marginal-bid :bid
                                 :marginal-cost :cost
                                 :marginal-clicks :clicks
                                 :marginal-impressions :impressions})
                 (sort-by :bid))
        butlst (butlast pts)
        rst (rest pts)]
    (->>
     (map #(merge-with - %2 %1)
          ;; On one of the maps to be merged, only select the marginal
          ;; keys (so that they can be subtracted and actually become
          ;; marginal). Leave the other keys holding strings, etc. alone.
          (h/select-cols
           [;; :marginal-revenue
            :marginal-bid :marginal-cost :marginal-clicks :marginal-impressions]
           butlst)
          rst)
     (cons (first pts)))))


(s/fdef class-marginals
  :args (s/cat :map-entry (s/tuple (constantly true)
                                   (constantly true)))
  :ret ::marginal-performances)

(defn class-marginals
  "Takes a map-entry of keydev to a list of performance-maps.

  Returns a flat list of performances-estimate maps, with keys:
  [:clicks :cost :bid :marginal-clicks :marginal-cost :marginal-bid]"
  [[keydev perfs]]
  (->> (zipmap (map :bid perfs)
               (derive-margins perfs))
       ;; Assoc the correct bid amounts in each map
       (map (fn [[bid m]]
              (assoc m :bid bid)))))


(defn marginal-hull
  [points]
  (let [points (sort-by :bid points)]
    (filter (fn [[fst scd]]
              (< (efficiency fst) (efficiency scd)))
            (partition-all 2 1 points))))


;; (s/fdef marginal-landscape
;;   :args (s/cat :objective #{:clicks :revenue :conversions :impressions}
;;                :landscape ::estimate/performance-estimates))

(defn marginal-landscape
  "Divides LANDSCAPE into marginals, then sorts the marginals in a way which
  maximizes OBJECTIVE."
  [objective landscape]
  (let [marginal-efficiency #(double (efficiency (u/marginal-kw objective) %))
        identifier (juxt (if (:keyword-id (set (keys (first landscape))))
                           :keyword-id
                           :key)
                         :device)
        res (->> landscape
                 (group-by identifier)
                 (pmap (fn [[k v]] [k (conv-hull/upper-left-hull objective v)]))
                 (mapcat class-marginals)
                 (h/derive-cols {:marginal-efficiency marginal-efficiency})
                 (sort-by :marginal-efficiency >))]
    res))


(s/fdef fit-to-budget
  :args (s/cat :budget integer? :marginals (s/coll-of map?))
  :ret (s/coll-of map?))

(defn fit-to-budget
  "Takes marginal segments from the total landscape pool until budget is
  exhausted."
  [budget marginals]
  (loop [todo (sort-by :marginal-efficiency > marginals)
         acc []]
    (if (or (empty? todo)
            (> (+ (:marginal-cost (first todo)) (h/sum :marginal-cost acc)) budget))
      acc
      (recur (next todo)
             (conj acc (first todo))))))


(defn bid-vectors
  "From an exploded list of marginal bids, groups by keydev and finds only the
  highest (i.e. final) bids."
  [optimized-marginals]
  (->> optimized-marginals
       (group-by u/keydev)
       (map (fn [[k v]]
              (let [max-res (apply max-key :bid v)]
                [(conj k (:bid max-res))
                 max-res])))
       (into {})))

(st/instrument)
