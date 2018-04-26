(ns trendtracker.forms.adgroup-search
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.core :as pl]))

(defrecord AdgroupSearchForm [])

(defmethod forms-core/submit-data AdgroupSearchForm [_ _ _ data]
  (pipeline! [value app-db]
    (pl/redirect!
     (let [q (:adgroup-search data)]
       (if (not-empty q)
         (assoc (-> app-db :route :data) :aq q)
         (dissoc (-> app-db :route :data) :aq))))))

(defn constructor []
  (->AdgroupSearchForm))

