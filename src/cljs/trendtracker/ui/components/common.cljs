(ns trendtracker.ui.components.common
  (:require [antizer.reagent :as ant]
            [reagent.core :as r]
            [trendtracker.utils :as u]))

(defn delta-widget
  ([d]
   (delta-widget d false))
  ([d down-is-good?]
   (if (or (pos? d) (neg? d))
     [:div
      {:style {:min-width 90
               :color (if down-is-good?
                        (if (pos? d) "#f5222d" "#389e0d")
                        (if (pos? d) "#389e0d" "#f5222d"))
               :margin-bottom 3}}
      [:span (if (js/isFinite d)
               (u/pct-fmt 2 d)
               "∞")]
      [ant/icon {:type (if (pos? d) "arrow-up" "arrow-down")}]]
     [:div "--"])))

(defn title-w-info [title info]
  (r/as-element
   [ant/row {:type "flex" :justify "space-between"}
    title
    [ant/tooltip {:title info}
     [ant/icon {:type "info-circle-o"
                :style {:margin-left 8}}]]]))

(def naver-icon
  [:img {:src "/img/logo/naver/square_green.png"
         :height "16"
         :style {:margin-right "6px"}}])

