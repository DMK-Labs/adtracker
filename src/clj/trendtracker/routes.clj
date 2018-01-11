(ns trendtracker.routes
  (:require [clojure.java.io :as io]
            [compojure.api.sweet :as sweet]
            [compojure.route :refer [resources]]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :refer [response]]
            [schema.coerce :as coerce]
            [schema.core :as s]
            [trendtracker.db :as db]
            [clojure.string :as string]))

(defn app-routes [endpoint]
  (sweet/routes
   (resources "/")
   (sweet/GET "*" _
     (-> "public/index.html"
         io/resource
         io/input-stream
         response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))))

(s/defschema Perf
  {:impressions s/Int
   :revenue s/Int
   :roas s/Num
   :during s/Inst
   :clicks s/Int
   :profit s/Num
   :conversions s/Int
   :ad_rank_sum s/Int
   :cost s/Int
   :cvr s/Num
   :ctr s/Num
   :customer_id s/Int})

(def coerce-perf
  (coerce/coercer Perf coerce/json-coercion-matcher))

(defn api-routes [{db :db}]
  (sweet/api
   {:swagger
    {:ui "/api-docs"
     :spec "/swagger.json"
     :data {:info {:title "Trend Tracker API"
                   :description "Use it or lose it."}}}}

   (sweet/context "/api" []
     :tags ["api"]

     (sweet/GET "/plus" []
       :summary "adding two numbers"
       :query-params [x :- Long y :- Long]
       (ok {:result (+ x y)}))

     (sweet/GET "/stats" []
       :summary "Customer's stats"
       :query-params [dates :- String]
       :return [Perf]
       (let [[low high] (string/split dates #",")]
         (ok (map
              coerce-perf
              (db/total-perf-by-date
               db
               {:id 777309 :low low :high high}))))))))
