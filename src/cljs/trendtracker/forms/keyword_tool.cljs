(ns trendtracker.forms.keyword-tool
  (:require [clojure.string :as string]
            [forms.validator :as v]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [trendtracker.api :as api]
            [trendtracker.forms.validators :as validators]))

(def validator
  (v/validator
   {:keywords [[:not-empty validators/not-empty?]]}))

(defrecord KeywordToolForm [validator])

(defmethod forms-core/submit-data KeywordToolForm [_ _ _ data]
  (pipeline! [value app-db]
    (print "Sending form..." data)
    ;; Save the queried keywords, to be used as default-value upon returning to
    ;; form:
    (pl/commit! (assoc-in app-db [:kv :keyword-tool :query] (:keywords data)))
    (api/first-place-stats
     (set (string/split-lines (:keywords data))))))

(defmethod forms-core/on-submit-success KeywordToolForm [this app-db form-id res]
  (pipeline! [value app-db]
    (pl/commit! (assoc-in app-db [:kv :keyword-tool :result] res))
    (pl/redirect! {:page "keyword-tool" :subpage "result"})))

(defn constructor []
  (->KeywordToolForm validator))
