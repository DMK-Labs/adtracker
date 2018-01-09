(ns trendtracker.ui.components.date-range
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [:div
   [:a [ant/icon {:type "left"}]]
   [ant/date-picker-range-picker
    {:style {:margin "0 8px"}
     :format "YYYY-MM-DD"
     :allow-clear false
     :disabled-date (constantly false) ;; FIXME
     :value [(js/moment) (js/moment)]
     ;; :ranges []
     }]
   [:a [ant/icon {:type "right"}]]])

(def component (ui/constructor {:renderer render}))
