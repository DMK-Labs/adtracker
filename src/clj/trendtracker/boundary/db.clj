(ns trendtracker.boundary.db
  (:require [specql.core :as specql]
            [trendtracker.config :refer [config]]))

;; (specql/define-tables
;;  (:db-spec config)
;;  ["access_rights" :foo/bar]
;;  [""])

;; (specql/fetch
;;  (:db-spec config)
;;  :foo/bar
;;  (specql/columns :foo/bar)
;;  {})
