(ns trendtracker.forms
  (:require [trendtracker.forms.keyword-tool :as keyword-tool]))

(def forms
  {:keyword-tool (keyword-tool/constructor)})

(def forms-ids
  {:login (fn [{:keys [page]}]
            (when (= "login" page)
              :form))
   :register (fn [{:keys [page]}]
               (when (= "register" page)
                 :form))
   :keyword-tool (fn [{:keys [page]}]
                   (when (= "keyword-tool" page)
                     :form))})
