(ns trendtracker.models.keywords
  (:require [hugsql.core :as hugsql]
            [naver-searchad.api.adkeyword :as adkeyword]
            [trendtracker.config :refer [config]]))

(def creds (:naver-creds config))

(def db-fns (hugsql/map-of-db-fns "sql/keywords.sql"))

(hugsql/def-db-fns "sql/keywords.sql")

;; (all (:db-spec config) {:customer-id 137307})
