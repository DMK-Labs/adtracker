(ns trendtracker.controllers
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.forms.controller :as forms-controller]
            [keechma.toolbox.forms.mount-controller :as forms-mount-controller]
            [trendtracker.controllers.cascader :as cascader]
            [trendtracker.controllers.current-client :as current-client]
            [trendtracker.controllers.date-range :as date-range]
            [trendtracker.controllers.logout :as logout]
            [trendtracker.controllers.redirect :as redirect]
            [trendtracker.datasources :refer [datasources]]
            [trendtracker.edb :refer [edb-schema]]
            [trendtracker.forms :refer [forms forms-ids]]
            [trendtracker.controllers.optimize :as optimize]))

(def controllers
  (-> {:redirect       redirect/controller
       :date-range     date-range/controller
       :cascader       cascader/controller
       :logout         logout/controller
       :current-client current-client/controller
       :optimize       optimize/controller}
      (forms-controller/register forms)
      (forms-mount-controller/register forms-ids)
      (dataloader-controller/register datasources edb-schema)))
