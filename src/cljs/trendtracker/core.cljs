(ns trendtracker.core
  (:require [keechma.app-state :as app-state]
            [trendtracker.app :as app]))

(defonce running-app (clojure.core/atom nil))

(defn start-app! []
  (reset! running-app (app-state/start! app/definition)))

(defn dev-setup []
  (when ^boolean js/goog.DEBUG
    (enable-console-print!)
    (println "dev mode")))

(defn reload []
  (let [current @running-app]
    (if current
      (app-state/stop! current start-app!)
      (start-app!))))

(defn ^:export main []
  (dev-setup)
  (start-app!))
