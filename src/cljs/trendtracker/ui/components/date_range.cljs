(ns trendtracker.ui.components.date-range
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [trendtracker.utils :as u]))

(defn render [ctx]
  [:div
   [:a [ant/icon {:type "left"}]]
   [ant/date-picker-range-picker
    {:style {:margin "0 8px"}
     :format "YYYY-MM-DD"
     :allow-clear false
     :disabled-date (constantly false) ;; FIXME
     :on-change #(ui/redirect
                  ctx
                  (merge (:data @(ui/current-route ctx))
                         {:dates %2}))
     ;; :ranges []
     }]
   [:a [ant/icon {:type "right"}]]])

(def component
  (ui/constructor {:renderer render}))
