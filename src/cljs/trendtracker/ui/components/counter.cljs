(ns trendtracker.ui.components.counter
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [:div
   [:div.content-header
    [:h3 "Simple Counter"]]
   [:div.content
    [ant/card
     [ant/button {:on-click #(<cmd ctx :update :dec)} "Decrement"]
     [ant/button {:on-click #(<cmd ctx :update :inc)} "Increment"]
     [:p (str "Count: " (sub> ctx :counter))]]]])

(def component
  (ui/constructor {:renderer render
                   :topic :counter
                   :subscription-deps [:counter]}))
