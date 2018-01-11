(ns trendtracker.ui.components.date-range
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [trendtracker.utils :as u]
            [keechma.toolbox.ui :refer [<cmd]]
            [clojure.string :as string]))

(defn render [ctx]
  [:div
   [:a [ant/icon {:type "left"}]]
   [ant/date-picker-range-picker
    {:style {:margin "0 8px"}
     :format "YYYY-MM-DD"
     :allow-clear false
     :disabled-date (constantly false) ;; FIXME
     :on-change #(<cmd ctx :update %2)
     :value (map js/moment (-> @(ui/current-route ctx)
                               :data
                               :dates
                               (string/split #",")))}]
   [:a [ant/icon {:type "right"}]]])

(def component
  (ui/constructor
   {:renderer render
    :topic :date-range}))
