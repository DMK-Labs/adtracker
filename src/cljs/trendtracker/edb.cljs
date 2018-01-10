(ns trendtracker.edb
  (:require [entitydb.core]
            [keechma.toolbox.edb :refer-macros [defentitydb]]))

(def edb-schema
  {:stats {:id :id}})

(defentitydb edb-schema)
