(ns trendtracker.ui.components.common
  (:require [antizer.reagent :as ant]
            [reagent.core :as r]
            [trendtracker.utils :as u]))

(defn delta-widget
  ([d]
   (delta-widget d false))
  ([d down-is-good?]
   (if (zero? d)
     [:div "--"]
     [:div
      {:style {:min-width 90
               :color (if down-is-good?
                        (if (pos? d) "#ff4d4f" "#52c41a")
                        (if (pos? d) "#52c41a" "#ff4d4f"))}}
      [:span (u/pct-fmt 2 d)]
      [ant/icon {:type (if (pos? d)
                         "arrow-up"
                         "arrow-down")}]])))

(defn title-w-info [title]
  (r/as-element
    [:div title [ant/icon {:type "info-circle-o"
                           :style {:margin-left 8}}]]))
