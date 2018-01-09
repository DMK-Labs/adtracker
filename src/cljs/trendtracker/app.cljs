(ns trendtracker.app
  (:require [trendtracker.controllers.core :refer [controllers]]
            [trendtracker.subscriptions :refer [subscriptions]]
            [trendtracker.ui.core :refer [ui]]))

(def definition
  {:components    ui
   :controllers   controllers
   :subscriptions subscriptions
   :html-element  (.getElementById js/document "app")
   :routes        [["" {:page "login"}]
                   ":page"]})
