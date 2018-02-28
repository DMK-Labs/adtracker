(ns trendtracker.ui.components.date-range
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [keechma.ui-component :as ui]
            [trendtracker.controllers.date-range :as date-range-ctrl]))

(defn- today-or-after? [d]
  (>= d (.startOf (js/moment) "day")))

(defn- near-end [current-range]
  (let [[_ high] (date-range-ctrl/next-range current-range)]
    (today-or-after? high)))

(defn render [ctx]
  (let [date-range (sub> ctx :date-range)
        current-range (:curr date-range)]
   [:span
    [ant/button {:icon "left" :on-click #(<cmd ctx :set-prev)}]
    [ant/date-picker-range-picker
     {:style {:margin "0 2px" :width 272}
      :format "YYYY-MM-DD"
      :allow-clear false
      :disabled-date today-or-after?
      :value current-range
      :on-change #(<cmd ctx :set %)
      :ranges (date-range-ctrl/presets)}]
    [ant/button {:icon "right" :on-click #(<cmd ctx :set-next)
                 :disabled (near-end current-range)}]]))

(def component
  (ui/constructor
   {:renderer render
    :topic :date-range
    :subscription-deps [:date-range]}))
