(ns trendtracker.forms.login
  (:require [keechma.toolbox.forms.core :as forms-core]
            [forms.validator :as v]
            [trendtracker.forms.validators :as validators]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [hodgepodge.core :refer [set-item local-storage]]
            [trendtracker.api :as api]
            [trendtracker.edb :refer [insert-named-item get-named-item]]))

(def validator
  (v/validator
   {:email [[:not-empty validators/not-empty?]
            [:email validators/email?]]
    :password [[:not-empty validators/not-empty?]]}))

(defrecord LoginForm [validator])

(defmethod forms-core/submit-data LoginForm [_ _ _ data]
  #_(api/login data))

(defmethod forms-core/on-submit-success LoginForm [this app-db form-id user]
  #_(let [jwt (:token user)]
      (pipeline! [value app-db]
        (set-item local-storage "trendtracker-jwt-token" jwt)
        (pl/commit! (-> app-db
                        (assoc-in [:kv :jwt] jwt)
                        (insert-named-item :user :current user)))
        (pl/redirect! {:page "dashboard"}))))

(defn constructor []
  (->LoginForm validator))
