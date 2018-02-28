(ns trendtracker.ui.components.summary
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> route>]]
            [trendtracker.utils :as u]))

(defn render [ctx]
  [ant/card
   [:ul.summaries
    [:li.summary {:style {:border-right "1px solid #e6ebf1"}}
     [:div {:style {:margin-bottom "0.5em"}}
      [:span.summary-title (u/krw 3000000)]
      [ant/icon {:type "edit"}]]
     [:p "예산"]]
    [:li.summary {:style {:border-right "1px solid #e6ebf1"}}
     [:div {:style {:margin-bottom 4}}
      [:span.summary-title "2018-02-22 ~ "]
      [ant/icon {:type "edit"}]]
     [:p "기간"]]
    [:li.summary
     [:div {:style {:margin-bottom 4}}
      [:span.summary-title {:style {:text-transform "capitalize"}} "Clicks"]
      [ant/icon {:type "edit"}]]
     [:p "목표 지표"]]]])

(def component
  (ui/constructor
   {:renderer render}))
