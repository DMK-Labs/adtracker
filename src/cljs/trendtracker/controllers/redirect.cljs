(ns trendtracker.controllers.redirect
  (:require [keechma.toolbox.pipeline.core :as pl :refer-macros [pipeline!]]
            [keechma.toolbox.pipeline.controller :as pl-controller]
            [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [trendtracker.edb :refer [get-named-item]]))

(defn get-redirect [route app-db]
  (let [current-user   (get-named-item app-db :user :current)
        client         (get-in app-db [:kv :current-client])
        just-logged-in (and (nil? (:page route)) current-user)]
    (cond
      just-logged-in        {:page "dashboard"}
      (not current-user)    ::trendtracker
      (not (:client route)) (assoc route :client (:customer_id client))
      :else                 nil)))

(defn redirect! [route app-db]
  (let [redirect-to (get-redirect route app-db)]
    (when redirect-to
      (if (= redirect-to ::trendtracker)
        (.assign js/window.location "http://trendtracker.co.kr")
        (pl/redirect! redirect-to)))))

(def controller
  (pl-controller/constructor
   (fn [{:keys [data]}] data)
   {:start (pipeline! [value app-db]
             (dataloader-controller/wait-dataloader-pipeline!)
             (redirect! value app-db))}))
