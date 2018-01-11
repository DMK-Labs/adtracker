(ns trendtracker.datasources
  (:require [keechma.toolbox.ajax :as ajax]
            [keechma.toolbox.dataloader.subscriptions :refer [map-loader]]))

(def ignore-datasource :keechma.toolbox.dataloader.core/ignore)

(def stats-datasource
  {:target [:kv :stats]
   :params (fn [prev route-data deps]
             (when (#{"dashboard"} (:page route-data))
               (:dates route-data)))
   :loader (map-loader
            (fn [req]
              (when (:params req)
                ;; (cljs.pprint/pprint req)
                (ajax/GET "/api/stats"
                          {:params
                           {:dates (-> req :app-db :route :data :dates)}}))))})

(def datasources
  {:stats stats-datasource})
