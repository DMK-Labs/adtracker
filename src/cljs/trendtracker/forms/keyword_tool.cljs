(ns trendtracker.forms.keyword-tool
  (:require [forms.validator :as v]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [trendtracker.forms.validators :as validators]
            [trendtracker.api :as api]
            [clojure.string :as string]))

(def validator
  (v/validator
   {:keywords [[:not-empty validators/not-empty?]]}))

(defrecord KeywordToolForm [validator])

(defmethod forms-core/submit-data KeywordToolForm [_ _ _ data]
  (api/first-place-stats (string/split-lines (:keywords data))))

(defmethod forms-core/on-submit-success KeywordToolForm [this app-db form-id res]
  (pipeline! [value app-db]
    (pl/commit! (assoc-in app-db [:kv :keyword-tool] res))))

(defn constructor []
  (->KeywordToolForm validator))
