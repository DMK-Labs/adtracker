(ns trendtracker.ad.keyword-tool
  (:require [naver-searchad.api.estimate :as estimate]
            [naver-searchad.api.related :as related]
            [trendtracker.config :refer [config]]
            [clojure.set :as set]
            [clojure.walk :as w]))

(defn first-place [kws]
  (estimate/nth-place-stats (:naver-creds config) 1 (set kws)))

(defn fifth-place [kws]
  (estimate/nth-place-stats (:naver-creds config) 5 (set kws)))

(defn nth-place [n kws]
  (estimate/nth-place-stats (:naver-creds config) n (set kws)))

(defn rel-kws [{:keys [keywords month show-detail] :as params}]
  (let [kw-partitions (partition-all 5 keywords)]
    (mapcat #(->> (assoc params :hint-keywords %)
                  (related/get (:naver-creds config))
                  :body
                  :keywordList)
            kw-partitions)))

(defn split-mobile-and-pc-query-counts [rel]
  (mapcat (fn [m]
            [(-> m
                 (assoc :device "MOBILE")
                 (dissoc :monthlyPcQcCnt))
             (-> m
                 (assoc :device "PC")
                 (dissoc :monthlyMobileQcCnt))])
          rel))

(defn rename-keys [rel]
  (w/postwalk (fn [x]
                (case x
                  :relKeyword :keyword
                  :monthlyMobileQcCnt :monthly-queries
                  :monthlyPcQcCnt :monthly-queries
                  x))
              rel))

(defn process [{:keys [keywords include-related?]}]
  (if include-related?
    (let [related (->> {:keywords keywords}
                       rel-kws
                       split-mobile-and-pc-query-counts
                       rename-keys)
          kws (map :keyword related)]
      (set/join (set/join (set/join (first-place kws)
                                    (nth-place 2 kws)
                                    {:keyword :keyword :device :device})
                          (nth-place 5 kws)
                          {:keyword :keyword :device :device})
                related
                {:keyword :keyword :device :device}))

    (set/join (set/join (first-place keywords)
                        (nth-place 2 keywords)
                        {:keyword :keyword :device :device})
              (nth-place 5 keywords)
              {:keyword :keyword :device :device})))

(defn simple-process [keywords include-related?]
  (let [kws (if include-related?
              (map :relKeyword (rel-kws {:keywords keywords}))
              keywords)]
    (set/join (set/join (first-place kws)
                        (nth-place 2 kws)
                        {:keyword :keyword :device :device})
              (nth-place 5 kws)
              {:keyword :keyword :device :device})))

(comment
  (time
   (simple-process ["맛집" "성형" "성형추천" "단기대출" "로션" "플라젠트라" "튼살크림"] false)))
