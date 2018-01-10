(ns trendtracker.config
  (:require [environ.core :refer [env]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]))

(defn config []
  {:http-port    (Integer. (or (env :port) 10555))
   :middleware   [[wrap-defaults site-defaults]
                  wrap-gzip]
   :database-url (env :database-url)})
