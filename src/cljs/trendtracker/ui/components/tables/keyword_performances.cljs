(ns trendtracker.ui.components.tables.keyword-performances
  (:require [keechma.ui-component :as ui]
            [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub> route>]]
            [trendtracker.utils :as u]
            [reagent.core :as r]
            [goog.string :as gstring]))

(defn render [ctx]
  [])

(def component
  (ui/constructor
   {:renderer render}))
