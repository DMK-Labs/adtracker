(ns trendtracker.datasources
  (:require [keechma.toolbox.ajax :as ajax]
            [keechma.toolbox.dataloader.subscriptions :refer [map-loader]]
            [promesa.core :as p]
            [trendtracker.utils :as u]))

(def default-request-config
  {:response-format :json
   :keywords? true
   :format :json})

(def ignore-datasource :keechma.toolbox.dataloader.core/ignore)

(def pass-through-params
  (map-loader
   (fn [req] (:params req))))

(def date-range-datasource
  {:target [:kv :date-range]
   :params (fn [prev _ _] (:data prev))
   :loader pass-through-params})

(def stats-datasource
  "Stats depend on the date-range, so it will be reloaded whenever date-range
  changes."
  {:target [:kv :stats]
   :deps [:date-range]
   :params (fn [_ _ deps] (select-keys deps [:date-range]))
   :loader (map-loader
            (fn [req]
              (when-let [{:keys [curr prev]} (-> req :params :date-range)]
                (let [[low high] prev
                      [l2 h2] curr
                      res (p/all [(ajax/GET "/api/stats"
                                            {:params {:low (u/fmt-dt low)
                                                      :high (u/fmt-dt high)}})
                                  (ajax/GET "/api/stats"
                                            {:params {:low (u/fmt-dt l2)
                                                      :high (u/fmt-dt h2)}})])]
                  (p/then res (fn [[r1 r2]]
                                {:prev r1
                                 :curr r2}))))))})

(def datasources
  {:date-range date-range-datasource
   :stats stats-datasource})
