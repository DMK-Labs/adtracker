(ns trendtracker.ui.components.breadcrumbs
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [route>]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [ant/breadcrumb {:style {:margin-bottom 8}}
   ;; [ant/breadcrumb-item
   ;;  [:a {:href "http://www.trendtracker.co.kr/"} "TrendTracker"]]
   [ant/breadcrumb-item
    [:a {:href (ui/url ctx {:page ""})} "Ad Tracker"]]
   [ant/breadcrumb-item
    {:style {:text-transform "capitalize"}}
    (:page (route> ctx))]])

(def component
  (ui/constructor {:renderer render}))
