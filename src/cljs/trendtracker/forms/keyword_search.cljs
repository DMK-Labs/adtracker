(ns trendtracker.forms.keyword-search
  (:require [forms.validator :as v]
            [trendtracker.forms.validators :as validators]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.core :as pl]))

(def validator
  (v/validator
   {}))

(defrecord KeywordSearchForm [validator])

(defmethod forms-core/submit-data KeywordSearchForm [_ _ _ data]
  (pipeline! [value app-db]
    (pl/redirect!
     (let [q (:keyword-search data)]
       (if (not-empty q)
         (assoc (-> app-db :route :data) :q q)
         (dissoc (-> app-db :route :data) :q))))))

(defn constructor []
  (->KeywordSearchForm validator))

