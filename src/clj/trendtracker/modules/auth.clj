(ns trendtracker.modules.auth
  (:require [buddy.sign.jwt :as jwt]
            [clojure.string :as string]
            [trendtracker.config :as config :refer [config]]
            [trendtracker.models.users :as users]))

(defn generate-signature [email password]
  (when-let [user (as-> {:email email :password password} $
                    (users/user (:db-spec config) $)
                    (dissoc $ :password))]
    (jwt/sign (assoc user :email email) config/secret)))

(defn logged-in-info [email password]
  (when-let [user (as-> {:email email :password password} $
                    (users/user (:db-spec config) $)
                    (dissoc $ :password))]
    (assoc user :token (jwt/sign user config/secret))))

(defn unsign-token [token]
  (jwt/unsign token config/secret))

(defn unsign-auth-header [s]
  (-> s
      (string/split #" ")
      second
      unsign-token))

(unsign-token "eyJhbGciOiJIUzI1NiJ9.eyJpZCI6MywiZW1haWwiOiJoZXkwa2ltQGRhdGFtYXJrZXRpbmcuY28ua3IiLCJuYW1lIjoiaGV5MCIsInRlbmFudCI6IkRhdGEgTWFya2V0aW5nIEtvcmVhIiwibmF2ZXJfaWQiOjcxOTQyNX0.h10aaUa4isCXcjKsAw8jK1NqZjIXvgK5zfG66n6JIoo")
