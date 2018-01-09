(ns trendtracker.controllers.core
  (:require [trendtracker.controllers.counter :as counter]))

(def controllers
  {:counter counter/controller})
