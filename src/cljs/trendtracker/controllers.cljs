(ns trendtracker.controllers
  (:require [keechma.toolbox.dataloader.controller :as dataloader-controller]
            [trendtracker.controllers.date-range :as date-range]
            [trendtracker.datasources :refer [datasources]]
            [trendtracker.edb :refer [edb-schema]]))

(def controllers
  (-> {:date-range date-range/controller}
      (dataloader-controller/register datasources edb-schema)))
