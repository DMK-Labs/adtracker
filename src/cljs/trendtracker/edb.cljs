(ns trendtracker.edb
  (:require [entitydb.core]
            [keechma.toolbox.edb :refer-macros [defentitydb]]))

(def edb-schema
  {:user {:id :email}
   :managed-clients {:id :customer_id}})

(defentitydb edb-schema)
