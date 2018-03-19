(ns optimus.utils
  (:require [clojure.set :as set]
            [huri.core :as h :refer :all]))

(defn round-to
  "Round a double to the given precision (number of significant digits)"
  [precision d]
  (let [factor (Math/pow 10 precision)]
    (/ (Math/round (* d factor)) factor)))

(defn- pt->xy
  [[x y]]
  {:x x :y y})

(defn pts->coords
  [points]
  (map pt->xy points))

(defn- xy->pt
  [{:keys [x y]}]
  [x y])

(defn coords->pts
  [coords]
  (map xy->pt coords))

(def keydev 
  (juxt :key :device))

(def summary (juxt #(sum :cost %) #(sum :clicks %)))

(defn doseq-interval
  "Works like `map`, but takes an additional arg INTERVAL which is the number of
  milliseconds to sleep in between function calls."
  [f coll interval]
  (doseq [x coll]
    (Thread/sleep interval)
    (f x)))

(def n* (fnil * 1))

(def n- (fnil - 0))

(defn divide-unless-zero
  [a b]
  (double (if (or (zero? a) (zero? b))
            0
            (/ a b))))

(defn nsd-map
  "`n` is either an *ns* object or a string representing a namespace."
  [n m]
  (reduce-kv (fn [acc k v]
               (let [new-kw (if (and (keyword? k)
                                     (not (qualified-keyword? k)))
                              (keyword (str n) (name k))
                              k) ]
                 (assoc acc new-kw v)))
             {} m))

(defn ns-kw
  "Prefixes `k` with `n`, where `n` is a *ns* or a string."
  [n k]
  (keyword (str n) (name k)))

(defn marginal-kw
  "Prefixes 'keyword' with 'marginal-'"
  [k]
  (keyword (str "marginal-" (name k))))

(defn is-sorted-by? [sort-fn comparator coll]
  (= coll
     (sort-by sort-fn comparator coll)))

(defn df->jutsu [df key-rename-map]
  (set/rename-keys (h/col)))

(defn summary [fitted]
  (let [cost (h/sum :marginal-cost fitted)
        clicks (h/sum :marginal-clicks fitted)
        revenue (h/sum :marginal-revenue (h/derive-cols {:marginal-revenue #((fnil :marginal-revenue 0) %)} fitted))]
    {:cpc (double (/ cost clicks))
     :cost cost
     :clicks clicks
     :revenue revenue
     :roi (double (/ revenue cost))}))

(defn first-where
  "Like `huri.core/where`, except it returns the first element rather than a list
  of all matches. For use when you know there will only be one match."
  [filters df]
  (let [filtered (h/where filters df)]
    (first filtered)))

(defn ns-qual-keys [m]
  (into (empty m)
        (map (fn [[k v]] [(keyword (str *ns*) (name k)) v]))
        m))
