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
      {:class (if down-is-good?
                (if (pos? d) "red" "green")
                (if (pos? d) "green" "red"))
       :style {:min-width 90}}
      [:span (u/pct-fmt 2 d)]
      [ant/icon {:type (if (pos? d)
                         "arrow-up"
                         "arrow-down")}]])))

(defn title-w-info [title]
  (r/as-element
    [:div title [ant/icon {:type "info-circle-o"
                           :style {:margin-left 8}}]]))
