(ns trendtracker.ui.components.cards.smart-todo
  (:require [antizer.reagent :as ant]
            [reagent.core :as r]
            [keechma.toolbox.ui :refer [route>]]
            [keechma.ui-component :as ui]))

(defn todo-card [ctx {:keys [title color description todos url-params]}]
  (let [current-route (route> ctx)]
    [ant/card {:title
               (r/as-element
                [:div [ant/icon {:type "bulb" :style {:margin-right 8 :color color}}] title])
               :extra (r/as-element [ant/icon {:type "info-circle-o"}])}
     [:p description]
     todos
     [:a {:href (ui/url ctx (merge current-route url-params))}
      [ant/button "확인하기"]]]))

(def component
  (ui/constructor
   {:renderer todo-card}))
