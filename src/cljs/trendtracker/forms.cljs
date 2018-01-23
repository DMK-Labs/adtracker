(ns trendtracker.forms
  (:require [trendtracker.forms.keyword-tool :as keyword-tool]
            [trendtracker.forms.optimize-objective :as optimize-objective]))

(def forms
  {:keyword-tool (keyword-tool/constructor)
   :optimize-objective (optimize-objective/constructor)})

(def forms-ids
  {;; :login (fn [{:keys [page]}]
   ;;          (when (= "login" page)
   ;;            :form))
   ;; :register (fn [{:keys [page]}]
   ;;             (when (= "register" page)
   ;;               :form))
   :keyword-tool (fn [{:keys [page]}]
                   (when (= "keyword-tool" page)
                     :form))
   :optimize-objective (fn [{:keys [page subpage step]}]
                         (when (and (= "optimize" page)
                                    (= "new" subpage)
                                    (= "1" step))
                           :form))})
