(ns trendtracker.utils
  (:require #?(:cljs goog.string.format)
            #?(:cljs [cljs.pprint :refer [cl-format]]
               :clj [clojure.pprint :refer [cl-format]])))

(defn sum [k df]
  (apply + (map k df)))

(defn delta [before after]
  (/ (- after before)
     before))

(defn avg [v]
  (/ (apply + v)
     (count v)))

;;* Formatting

(defn int-fmt
  "Puts commas in thousandth places. 1234567 => 1,234,567"
  [n]
  (cl-format nil "~:d" n))

(defn krw [num]
  (str "₩" (int-fmt num)))

(defn dec-fmt [decimals num]
  #?(:cljs (goog.string.format (str "%." decimals "f") num)))

(defn pct-fmt
  "Formats `decimal` as a %, to the `n`th place. 0.1234 => 12.34%"
  [n decimal]
  #?(:cljs (goog.string.format (str "%." n "f%") (* 100 decimal))))

(defn human-dttm [moment]
  (.format moment "YYYY-MM-DD HH:MMZ"))
