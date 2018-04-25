(ns trendtracker.forms.keyword-search
  (:require [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.core :as pl]))

(defrecord KeywordSearchForm [])

(defmethod forms-core/submit-data KeywordSearchForm [_ _ _ data]
  (pipeline! [value app-db]
    (pl/redirect!
     (let [q (:keyword-search data)]
       (if (not-empty q)
         (assoc (-> app-db :route :data) :kq q)
         (dissoc (-> app-db :route :data) :kq))))))

(defn constructor []
  (->KeywordSearchForm))

