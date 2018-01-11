(ns trendtracker.controllers.date-range
  (:require [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [trendtracker.utils :as u]
            [clojure.string :as string]))

(defn preset
  [count]
  (string/join
   ","
   (map u/fmt-dt [(.subtract (js/moment) count "d")
                  (.subtract (js/moment) 1 "d")])))

(defn presets []
  {:last-7-days (preset 7)
   :last-14-days (preset 14)
   :last-28-days (preset 28)
   :last-90-days (preset 90)
   :last-month [(.startOf (.subtract (js/moment) 1 "M") "M")
                (.endOf (.subtract (js/moment) 1 "M") "M")]})

(def controller
  (pl-controller/constructor
   (fn [route-params]
     (when (= (get-in route-params [:data :page]) "dashboard")
       true))
   {:start (pipeline! [value app-db]
             (pl/redirect!
              (merge (-> app-db :route :data)
                     {:dates (preset 90)})))
    :update (pipeline! [value app-db]
              (pl/redirect!
               (merge (-> app-db :route :data)
                      {:dates value})))}))
