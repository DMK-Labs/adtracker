(ns trendtracker.ui.pages.keyword-tool
    (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [:div.content-header
   [(ui/component ctx :breadcrumbs)]
   [:h2 "키워드 도구"]])

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs]}))
