(ns trendtracker.core
  (:require [keechma.app-state :as app-state]
            [trendtracker.controllers :refer [controllers]]
            [trendtracker.subscriptions :refer [subscriptions]]
            [trendtracker.ui :refer [ui]]))

(def app-definition
  {:components    ui
   :controllers   controllers
   :subscriptions subscriptions
   :html-element  (.getElementById js/document "app")
   :routes        [["" {:page "login"}]
                   ":page"]
   :router        :history})

(defonce running-app (clojure.core/atom nil))

(defn start-app! []
  (reset! running-app (app-state/start! app-definition)))

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "starting in dev mode")))

(defn reload []
  (let [current @running-app]
    (if current
      (app-state/stop! current start-app!)
      (start-app!))))

(defn ^:export main []
  (dev-setup)
  (start-app!))
