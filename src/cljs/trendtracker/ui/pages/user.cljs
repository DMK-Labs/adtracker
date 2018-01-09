(ns trendtracker.ui.pages.user
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [reagent.core :as r]))

(defn render [ctx]
  [:div.content
   [ant/card
    {:title (r/as-element
             [:span [ant/icon {:type "setting" :style {:margin-right 8}}]
              "사용자 설정"])}
    [:p "사용자 ID: dashcrab"]
    [:p "회사명: Nine Bridge, Inc."]
    [:p "연락처: 010-1212-3434"]]])

(def component
  (ui/constructor
   {:renderer render}))
