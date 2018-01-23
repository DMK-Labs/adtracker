(ns trendtracker.datasources
  (:require [keechma.toolbox.ajax :as ajax]
            [keechma.toolbox.dataloader.subscriptions :refer [map-loader]]
            [promesa.core :as p]
            [trendtracker.utils :as u]
            [cljs.core.match :refer-macros [match]]
            [trendtracker.api :as api]))

(def pass-through-params
  (map-loader
   (fn [req] (:params req))))

(defn total-perf
  [range]
  (-> [(api/total-performance (:curr range))
       (api/total-performance (:prev range))]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn campaign-perf
  [id range]
  (-> [(api/campaign-performance (:curr range) id)
       (api/campaign-performance (:prev range) id)]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(defn campaign-type-perf
  [type range]
  (-> [(api/campaign-type-performance type (:curr range))
       (api/campaign-type-performance type (:prev range))]
      p/all
      (p/then
       #(zipmap [:curr :prev] %))))

(def daily-stats-datasource
  "Stats depend on the date-range, so it will be reloaded whenever date-range
  changes."
  {:target [:kv :daily-stats]
   :deps [:date-range :cascader]
   :params (fn [_ _ deps]
             (select-keys deps [:date-range :cascader]))
   :loader (map-loader
            (fn [req]
              (let [range (get-in req [:params :date-range])
                    casc (get-in req [:params :cascader])]
                (when (and (seq range) (seq casc))
                  (match casc
                    ["total"] (total-perf range)
                    [type] (campaign-type-perf type range)
                    [type cmp-id] (campaign-perf cmp-id range))))))})

(def aggregate-stats-datasource
  {:target [:kv :aggregate-stats]
   :params (constantly true)
   :loader (map-loader
             (fn [req]
               ;; (ajax/GET "/api/stats/aggregate-segmented")
               ))})

(def datasources
  {:date-range {:target [:kv :date-range]
                :params (fn [prev _ _]
                          (:data prev))
                :loader pass-through-params}
   :cascader {:target [:kv :cascader]
              :params (fn [prev _ _]
                        (:data prev))
              :loader pass-through-params}
   :daily-stats daily-stats-datasource
   :aggregate-stats aggregate-stats-datasource})
