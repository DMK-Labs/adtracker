(ns trendtracker.ui.pages.optimize
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [:div.content-header
   [(ui/component ctx :breadcrumbs)]
   [:h2 "입찰 최적화"]])

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs]}))
