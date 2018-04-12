(ns naver-searchad.api.request
  (:require [aleph.http :as http]
            [byte-streams :as byte-streams]
            [clojure.data.codec.base64 :as base64]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [jsonista.core :as json]
            [manifold.deferred :as deferred]
            [pandect.algo.sha256 :as sha256]
            [taoensso.timbre :as timbre]))

(s/def ::creds (s/keys :req-un [::customer-id ::access-key ::private-key]))

(defn- signature
  "Uses sha256 HMAC to calculate the signature of the API request, returning a
  base64 encoded string."
  [timestamp method uri private-key]
  (-> (sha256/sha256-hmac-bytes
       (string/join "." [timestamp (.toUpperCase (name method)) uri])
       private-key)
      ^bytes base64/encode
      (String. "UTF-8")))

(defn- auth-header
  [timestamp {:keys [access-key customer-id private-key] :as creds} method uri]
  {:X-API-KEY access-key
   :X-Customer customer-id
   :X-Timestamp timestamp
   :X-Signature (signature timestamp method uri private-key)})

(defn json-read-and-keywordize
  "Can take File, String, Reader and InputStream, reading it as EDN with
  keywordized keys."
  [i]
  (json/read-value i (json/object-mapper {:decode-key-fn true})))

(defn raw
  [creds method uri params]
  (let [timestamp (System/currentTimeMillis)]
    (http/request
     (merge {:method method
             :url (str "https://api.naver.com" uri)
             :headers (auth-header timestamp creds method uri)
             :content-type :json
             :throw-exceptions? true}
            params))))

(defn request
  "METHOD is :post, :put, :get or :delete
  URI is a string including the prepending slash: '/ncc/campaigns'
  BODY is the body of the API request in edn form:
  {:items [{:keyword \"제주여행\",
            :bid 2000
            :device \"BOTH\",
            :keywordplus false}]}"
  [creds method uri params]
  (-> @(raw creds method uri (update params :body #(if % (json/write-value-as-string %) "")))
      (update :body byte-streams/to-string)
      (update :body json-read-and-keywordize)))

(defn GET
  ([creds uri]
   (GET creds uri {}))
  ([creds uri params]
   (request creds :get uri params)))

(defn POST
  ([creds uri]
   (POST creds uri {}))
  ([creds uri params]
   (request creds :post uri params)))

(defn PUT
  ([creds uri]
   (PUT creds uri {}))
  ([creds uri params]
   (request creds :put uri params)))

(defn DELETE
  ([creds uri]
   (DELETE creds uri {}))
  ([creds uri params]
   (request creds :delete uri params)))
