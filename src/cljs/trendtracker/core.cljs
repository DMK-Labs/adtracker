(ns trendtracker.core
  (:require [keechma.app-state :as app-state]
            [keechma.toolbox.dataloader.app :as dataloader]
            [keechma.toolbox.forms.app :as forms]
            [trendtracker.forms :as tt-forms]
            [trendtracker.controllers :refer [controllers]]
            [trendtracker.subscriptions :refer [subscriptions]]
            [trendtracker.datasources :refer [datasources]]
            [trendtracker.edb :refer [edb-schema]]
            [trendtracker.ui :refer [ui]]))

(def app-definition
  (-> {:components ui
       :controllers controllers
       :subscriptions subscriptions
       :html-element (.getElementById js/document "app")
       :routes [["" {:page "dashboard"}]
                ":page"
                ":page/:subpage"]}
      (dataloader/install datasources edb-schema)
      (forms/install tt-forms/forms tt-forms/forms-automount-fns)))

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
