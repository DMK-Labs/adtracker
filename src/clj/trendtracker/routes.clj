(ns trendtracker.routes
  (:require [clojure.java.io :as io]
            [compojure.api.sweet :as sweet]
            [compojure.route :refer [resources]]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :refer [response]]
            [schema.coerce :as coerce]
            [schema.core :as s]
            [trendtracker.db :as db]
            [trendtracker.utils :as u]))

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
   :roas Double
   :during s/Str
   :clicks s/Int
   :profit Double
   :conversions s/Int
   :ad_rank_sum s/Int
   :cost s/Int
   :cvr Double
   :ctr Double
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
       :query-params [low :- String high :- String]
       :return [Perf]
       (ok (into [] (comp
                     (map (fn [m] (update m :during u/iso-date)))
                     (map coerce-perf))
                 (db/total-perf-by-date db {:id 777309 :low low :high high})))))))
