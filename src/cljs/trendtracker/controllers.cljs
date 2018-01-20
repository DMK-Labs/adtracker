(ns trendtracker.controllers
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [keechma.toolbox.forms.controller :as forms-controller]
            [keechma.toolbox.forms.mount-controller :as forms-mount-controller]
            [trendtracker.controllers.cascader :as cascader]
            [trendtracker.controllers.date-range :as date-range]
            [trendtracker.datasources :refer [datasources]]
            [trendtracker.edb :refer [edb-schema]]
            [trendtracker.forms :refer [forms forms-ids]]))

(def controllers
  (-> {:date-range date-range/controller
       :cascader cascader/controller}
      (forms-controller/register forms)
      (forms-mount-controller/register forms-ids)
      (dataloader-controller/register datasources edb-schema)))
