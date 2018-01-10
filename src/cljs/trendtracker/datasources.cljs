(ns trendtracker.datasources
  (:require [keechma.toolbox.ajax :as ajax]
            [keechma.toolbox.dataloader.subscriptions :refer [map-loader]]))

(def ignore-datasource :keechma.toolbox.dataloader.core/ignore)

(def stats-datasource
  {:target [:edb/collection :stats/list]
   :params (fn [prev {:keys [page]} deps]
             (when (#{"dashboard" "manage" "optimize"} page) true))
   :loader (map-loader
            (fn [req]
              (when (:params req)
                (print "loading stats...")
                (ajax/GET "/api/stats"))))})

(def datasources
  {:stats stats-datasource})
