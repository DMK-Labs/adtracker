(ns trendtracker.config
  (:require [environ.core :refer [env]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]))

(def config
  {:http-port  (Integer. (or (env :port) 10555))
   :middleware [[wrap-defaults site-defaults]
                wrap-gzip]
   :db-spec    {:classname   "org.postgresql.Driver"
                :subprotocol "postgresql"
                :subname     (str "//"
                                  (env :db-host)
                                  ":"
                                  (or (env :db-port) "5432")
                                  "/"
                                  (env :db-name))
                :user        (env :db-user)
                :password    (env :db-password)}})
