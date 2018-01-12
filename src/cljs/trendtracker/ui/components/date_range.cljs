(ns trendtracker.ui.components.date-range
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [:div
   [:a [ant/icon {:type "left"}]]
   [ant/date-picker-range-picker
    {:style {:margin "0 8px" :width 272}
     :format "YYYY-MM-DD"
     :allow-clear false
     :disabled-date (constantly false) ;; FIXME
     :value (:curr (sub> ctx :date-range))
     :on-change #(<cmd ctx :set %)}]
   [:a [ant/icon {:type "right"}]]])

(def component
  (ui/constructor
   {:renderer render
    :topic :date-range
    :subscription-deps [:date-range]}))
