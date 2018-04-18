(ns trendtracker.forms
  (:require [trendtracker.forms.keyword-tool :as keyword-tool]
            [trendtracker.forms.optimize-objective :as optimize-objective]
            [trendtracker.forms.login :as login]))

(def forms
  {:keyword-tool (keyword-tool/constructor)
   :optimize-objective (optimize-objective/constructor)
   :login (login/constructor)})

(def forms-automount-fns
  {:login (fn [{:keys [page]}]
            (when (= "login" page)
              :form))
   ;; :register (fn [{:keys [page]}]
   ;;             (when (= "register" page)
   ;;               :form))
   :keyword-tool (fn [{:keys [page]}]
                   (when (= "keywords" page)
                     :form))
   :optimize-objective (fn [{:keys [page subpage step]}]
                         (when (and (= "optimize" page)
                                    (= "settings" subpage)
                                    (= "1" step))
                           :form))})
