(ns trendtracker.controllers.counter
  (:require [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pl-controller]))

(defn update-counter-value [action app-db]
  (let [current (get-in app-db [:kv :counter] 0)
        action-fn (if (= action :inc) inc dec)]
    (assoc-in app-db [:kv :counter] (action-fn current))))

(def controller
  (pl-controller/constructor
   (fn [_] true)
   {:start (pipeline! [value app-db]
             (pl/commit! (assoc-in app-db [:kv :counter] 0)))
    :update (pipeline! [value app-db]
              (pl/commit! (update-counter-value value app-db)))}))
