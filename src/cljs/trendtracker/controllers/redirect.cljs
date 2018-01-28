(ns trendtracker.controllers.redirect
  (:require [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [trendtracker.edb :refer [get-named-item]]))

(defn get-redirect [route app-db]
  (let [page         (:page route)
        subpage      (:subpage route)
        current-user (get-named-item app-db :user :current)
        dashboard    {:page "dashboard"}
        login        {:page "login"}]
    (cond
      (and (= "login" page) current-user) dashboard
      (not current-user)                  login
      :else                               nil)))

(defn redirect! [route app-db]
  (let [redirect-to (get-redirect route app-db)]
    (when redirect-to
      (pl/redirect! redirect-to))))

(def controller
  (pl-controller/constructor
   (fn [{:keys [data]}]
     data)
   {:start (pipeline! [value app-db]
             (dataloader-controller/wait-dataloader-pipeline!)
             (redirect! value app-db))}))
