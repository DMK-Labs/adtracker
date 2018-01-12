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
              (when-let [{:keys [curr prev]} (get-in req [:params :date-range])]
                ;; FIXME: for some reason this is run twice on page reload
                (print "Responding to changed filter, loading stats...")
                (let [parse-range #(->> %
                                        (map u/fmt-dt)
                                        (zipmap [:low :high]))]
                  (-> [(ajax/GET "/api/stats" {:params (parse-range curr)})
                       (ajax/GET "/api/stats" {:params (parse-range prev)})]
                      p/all
                      (p/then
                       #(zipmap [:curr :prev] %)))))))})

(def datasources
  {:date-range date-range-datasource
   :stats stats-datasource})
