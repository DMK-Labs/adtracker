(ns trendtracker.ui.pages.login
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [:div {:style {:max-width 500
                 :margin "128px"}}
   [ant/card
    [:h1 "Search Ad Management has never been easier"]
    [:p "We make it completely simple to automate, optimize, and manage your ads"]
    [:p>a {:href (ui/url ctx {:page "dashboard"})}
     "Login"]]])

(def component
  (ui/constructor
   {:renderer render}))
