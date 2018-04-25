(ns trendtracker.forms.creative-search
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.core :as pl]))

(defrecord CreativeSearchForm [])

(defmethod forms-core/submit-data CreativeSearchForm [_ _ _ data]
  (pipeline! [value app-db]
    (pl/redirect!
     (let [q (:creative-search data)]
       (if (not-empty q)
         (assoc (-> app-db :route :data) :cq q)
         (dissoc (-> app-db :route :data) :cq))))))

(defn constructor []
  (->CreativeSearchForm))

