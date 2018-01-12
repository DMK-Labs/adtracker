(ns trendtracker.ui.components.date-range
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.ui-component :as ui]
            [trendtracker.controllers.date-range :as date-range-ctrl]))

(defn render [ctx]
  [:div
   ;; TODO: impl left/right buttons
   [:a {:on-click #(print "go to prev")} [ant/icon {:type "left"}]]

   [ant/date-picker-range-picker
    {:style {:margin "0 8px" :width 272}
     :format "YYYY-MM-DD"
     :allow-clear false
     :disabled-date (constantly false) ;; FIXME
     :value (:curr (sub> ctx :date-range))
     :on-change #(<cmd ctx :set %)
     :ranges (date-range-ctrl/presets)}]

   [:a {:on-click #(print "go to next")} [ant/icon {:type "right"}]]])

(def component
  (ui/constructor
   {:renderer render
    :topic :date-range
    :subscription-deps [:date-range]}))
