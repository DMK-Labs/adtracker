(ns trendtracker.controllers
  (:require [trendtracker.controllers.cascader :as cascader]
            [trendtracker.controllers.current-client :as current-client]
            [trendtracker.controllers.date-range :as date-range]
            [trendtracker.controllers.logout :as logout]
            [trendtracker.controllers.redirect :as redirect]
            [trendtracker.controllers.optimize :as optimize]))

(def controllers
  {:redirect redirect/controller
   :date-range date-range/controller
   :cascader cascader/controller
   :logout logout/controller
   :current-client current-client/controller
   :optimize optimize/controller})
