(ns trendtracker.controllers
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [trendtracker.controllers.counter :as counter]
            [trendtracker.datasources :refer [datasources]]
            [trendtracker.edb :refer [edb-schema]]))

(def controllers
  (-> {:counter counter/controller}
      (dataloader-controller/register datasources edb-schema)))
