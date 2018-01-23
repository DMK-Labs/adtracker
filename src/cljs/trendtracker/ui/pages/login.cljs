(ns trendtracker.ui.pages.login
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.forms.helpers :as forms-helpers]))

(defn render [ctx]
  (let [form-id [:login :form]
        form-state @(forms-helpers/form-state ctx form-id)
        helpers (forms-helpers/make-component-helpers ctx form-id)]
    [:div {:style {:max-width 550
                   :margin "128px"}}
     [ant/card
      [:h1 "Search Ad Management has never been easier"]
      [:p "We make it completely simple to automate, optimize, and manage your ads"]
      [:p>a {:href (ui/url ctx {:page "dashboard"})}
       "Login"]]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:form-state]}))
