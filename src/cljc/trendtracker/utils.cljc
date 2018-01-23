(ns trendtracker.utils
  (:require #?(:cljs goog.string.format)
            #?(:cljs [cljs.pprint :refer [cl-format]]
               :clj [clojure.pprint :refer [cl-format]])
            #?(:clj [java-time :as time])))


;;* Maps and keys

(defn prefix-keys [m s]
  (into {} (map (fn [[k v]]
                  [(keyword (str s (name k))) v])
                m)))

;;* Math

(defn sum [k df]
  (apply + (map k df)))

(defn delta [before after]
  (/ (- after before)
     before))

(defn avg [v]
  (/ (apply + v)
     (count v)))

#?(:cljs
   (defn sorter-by [k]
     "Returns a sorting function which takes two maps and compares them by a
  common key `k`. Used to create a sorter fn for table columns."
     (fn [curr next]
       (apply - (map #(-> % (js->clj :keywordize-keys true) k)
                     [curr next])))))

;;* Formatting

(defn int-fmt
  "Puts commas in thousandth places. 1234567 => 1,234,567"
  [n]
  (cl-format nil "~:d" n))

(defn krw [num]
  (str "₩" (int-fmt num)))

(defn dec-fmt [decimals num]
  #?(:cljs (goog.string.format (str "%." decimals "f") num)))

#?(:cljs (defn pct-fmt
           "Formats `decimal` as a %, to the `n`th place. 0.1234 => 12.34%"
           ([decimal]
            (pct-fmt 2 decimal))
           ([n decimal]
            (goog.string.format (str "%." n "f%") (* 100 decimal)))))

(defn fmt-dt
  ([moment]
   (.format moment "YYYY-MM-DD"))
  ([moment format-str]
   (.format moment format-str)))

#?(:clj
   (defn iso-date [dt]
     (time/format :iso-date (time/local-date dt))))
