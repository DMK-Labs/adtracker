(ns optimus.conv-hull
  (:require [convex-hull.chans-algorithm :refer [chans-algorithm]]
            [optimus.utils :as u]
            [huri.core :as h]))

(defn filter-dominants
  "Given an objective and a set of estimates (assumed to be of the same keydev
  class), removes the obviously dominated points. Points which deliver the same
  objective for more cost are obviously dominated. Should be idempotent."
  [objective estimates]
  (->> estimates
       (group-by objective)
       (map (fn [[_ v]]
              (first (sort-by :cost v))))))

(defn lp-dominated?
  "Takes a grouping of 3 or fewer points. If given 3 points, check whether the
  center point is LP dominated by the surrounding two. If given 2 or fewer
  points, simply returns `false`.

  (Because a point cannot dominate itself, nor should a single point LP
  dominate another, as long as we call `filter-dominants` on the list of points
  first.)"
  [objective points]
  (let [[r s t] points]
    (let [[[wr pr] [ws ps] [wt pt]] (map (juxt :cost objective) [r s t])]
      (and (<= wr ws wt)
           (<= pr ps pt)
           (pos? (- (* (- ws wr) (- pt pr))
                    (* (- ps pr) (- wt wr))))))))

(defn filter-lp-dominants
  "Partitions the given set of points by 3, moving by 1 (i.e. each inner point
  should be the center point once). For each partition, checks whether the center point is lp-dominated by the surrounding ones."
  [objective performances]
  (->> performances
       (group-by u/keydev)
       (mapcat (fn [[k v]] (->> (partition 3 1 v)
                             (filter #(lp-dominated? objective %))
                             (map second)
                             set)))
       (clojure.set/difference (set performances))
       (sort-by :cost)))

(defn hull
  [objective perfs]
  (if (<= (count perfs) 5)
    perfs
    (->> perfs
         (map #(clojure.set/rename-keys % {:cost :x objective :y}))
         chans-algorithm
         (map #(clojure.set/rename-keys % {:x :cost :y objective}))
         (sort-by :bid))))

(defn get-first-repeat
  "Given an infinite iteration, checks each iter until a repeat happens, and
  returns the repeat."
  [iters]
  (loop [inf-list iters
         acc #{}]
    (if (acc (first inf-list))
      (first inf-list)
      (recur (rest inf-list)
             (conj acc (first inf-list))))))

;; Results of `upper-left-hull` must be monotonically increasing keyed by
;; :clicks, :cost, :impressions, etc.
(defn upper-left-hull
  [objective points]
  (let [dominant (filter-dominants objective points)
        iters (iterate #(filter-lp-dominants objective %) dominant)]
    (sort-by objective (get-first-repeat iters))))
