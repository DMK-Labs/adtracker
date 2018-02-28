(ns trendtracker.ui.components.breadcrumbs
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [route>]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  (let [route (route> ctx)
        page (:page route)
        subpage (:subpage route)
        client (:client route)
        capitalize {:style {:text-transform "capitalize"}}]
    [ant/breadcrumb {:style {:margin-bottom 12}}
     [ant/breadcrumb-item
      [:a {:href (ui/url ctx {:page "dashboard" :client client})} "Ad Tracker"]]
     [ant/breadcrumb-item capitalize
      (if subpage
        [:a {:href (ui/url ctx {:page page :client client})} page]
        page)]
     (when subpage
       [ant/breadcrumb-item capitalize subpage])]))

(def component
  (ui/constructor {:renderer render}))
