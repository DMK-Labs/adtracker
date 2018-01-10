(ns trendtracker.ui.pages.user
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [reagent.core :as r]))

(defn render-current-user
  "Renders the user info based on loaded data."
  [ctx current-user]
  [:div.content
   [ant/card
    {:title "사용자 정보"}
    [:p "사용자 ID: " (:login-id current-user)]
    [:p "회사명: " (:company-name current-user)]
    [:p "연락처: " (:email current-user)]]])

(defn render [ctx]
  (render-current-user ctx {:login-id "dashcrab"
                            :company-name "Nine Bridge, Inc."
                            :email "test@test.com"}))

(def component
  (ui/constructor
   {:renderer render}))
