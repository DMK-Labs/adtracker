(ns naver-searchad.date-utils
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [java-time :as time]))

(s/def ::iso-8601 (s/and string? #(= 20 (count %))))

(s/def ::date-string
  ;; TODO: improve spec
  ;; Either YYYMMDD or yyyy-MM-ddTHH:mm:ssZ
  (s/or :KST (s/and string?
                    #(= 8 (count %)))
        :ISO8601 ::iso-8601))

(defn now
  "ISO-8601 formatted string of the current date time in UTC."
  []
  (time/format :iso-date-time
               (time/truncate-to
                (time/zoned-date-time (time/zone-id "Z"))
                :hours)))

(defn yesterday
  []
  (time/format :iso-date-time
               (time/minus (time/truncate-to
                            (time/zoned-date-time (time/zone-id "Z"))
                            :hours)
                           (time/days 1))))

(defn ago [interval]
  (time/minus (time/local-date) interval))

(defn last-year []
  (-> 1 time/years ago))

(defn iso-date [dt]
  (time/format :iso-date (time/local-date dt)))

(defn yyyymmdd
  "Given a date object, prints out a string in yyyyMMdd form."
  [dt]
  (when dt (string/replace (iso-date dt) #"-" "")))

(defn days-between
  "Excludes start and end date."
  [start end]
  (take-while
   #(time/before? % end)
   (rest (time/iterate time/plus
                  start
                  (time/days 1)))))

(defn days-since
  "Args are applied to `java-time/local-date`. Returns a list of dates starting
  after the given one, up to and including yesterday."
  [local-date]
  (days-between (time/local-date local-date)
                (time/local-date)))

(defn parse-yyyymmdd
  "Parses string of form \"20171011\" into a java.time.LocalDate"
  ([s]
   (time/local-date "yyyyMMdd" s))
  ([s sql?]
   (time/sql-date (time/local-date "yyyyMMdd" s))))

(defn last-n-days
  "Generates a sequence of yyyyMMdd formatted strings of the last n dates:

  [20171101 20171102 20171103 ...]"
  [n]
  (map yyyymmdd
       (take n (rest (time/iterate
                      time/minus
                      (time/local-date)
                      (time/days 1))))))
