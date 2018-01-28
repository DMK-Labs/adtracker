(ns trendtracker.config
  (:require [environ.core :refer [env]]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults]]
            [ring.middleware.gzip :refer [wrap-gzip]]
            [ring.middleware.format :refer [wrap-restful-format]]
            [buddy.auth.middleware :refer [wrap-authentication wrap-authorization]]
            [buddy.auth.backends.token :refer [jws-backend]]))

(defonce secret "8455af0a69bd9a37c4d0a64eed6f80e82a98f67212dced25")

(def auth-backend (jws-backend {:secret secret}))

(def config
  {:http-port  (Integer. (or (env :port) 10555))
   :middleware [[wrap-authentication auth-backend]
                [wrap-authorization auth-backend]
                [wrap-defaults (-> site-defaults
                                   (assoc-in [:security :anti-forgery] false)
                                   (dissoc :session))]
                wrap-gzip]
   :api-middleware [wrap-restful-format]
   :db-spec    {:classname   "org.postgresql.Driver"
                :subprotocol "postgresql"
                :subname     (str "//"
                                  (env :db-host)
                                  ":"
                                  (or (env :db-port) "5432")
                                  "/"
                                  (env :db-name))
                :user        (env :db-user)
                :password    (env :db-password)}
   :naver-creds {:customer-id 719425
                 :access-key (env :x-api-key)
                 :private-key (env :x-private-key)}})
