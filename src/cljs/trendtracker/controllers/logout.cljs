(ns trendtracker.controllers.logout
  (:require [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [hodgepodge.core :refer [remove-item local-storage]]
            [trendtracker.edb :refer [remove-named-item]]))

(def controller
  (pl-controller/constructor
   (fn [{:keys [data]}]
     (when (= "logout" (:page data))
       true))
   {:start (pipeline! [_ app-db]
             (remove-item local-storage "lacuna-jwt-token")
             (pl/commit! (-> app-db
                             (assoc-in [:kv :jwt] nil)
                             (remove-named-item :user :current)))
             (pl/redirect! {:page "login"}))}))
