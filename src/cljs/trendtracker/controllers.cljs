(ns trendtracker.controllers
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [trendtracker.controllers.counter :as counter]
            [trendtracker.datasources :refer [datasources]]
            [trendtracker.edb :refer [edb-schema]]
            [trendtracker.controllers.date-range :as date-range]))

(def controllers
  (-> {:counter counter/controller
       :date-range date-range/controller}
      (dataloader-controller/register datasources edb-schema)))
