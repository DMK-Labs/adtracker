(ns trendtracker.routes
  (:require [clojure.java.io :as io]
            [compojure.api.sweet :as sweet]
            [compojure.route :refer [resources]]
            [naver-searchad.api.adgroup :as naver-adgroup]
            [naver-searchad.api.stats :as naver-stats]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :refer [response]]
            [schema.coerce :as coerce]
            [schema.core :as s]
            [trendtracker.config :refer [config]]
            [trendtracker.db :as db]
            [trendtracker.utils :as u]
            [trendtracker.ad.keyword-tool :as keyword-tool]))

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
  {(s/optional-key :impressions) s/Int
   (s/optional-key :revenue) s/Int
   (s/optional-key :roas) Double
   (s/optional-key :during) s/Str
   (s/optional-key :clicks) s/Int
   (s/optional-key :profit) Double
   (s/optional-key :conversions) s/Int
   (s/optional-key :ad_rank_sum) s/Int
   (s/optional-key :cost) s/Int
   (s/optional-key :cvr) Double
   (s/optional-key :ctr) Double
   (s/optional-key :customer_id) s/Int
   (s/optional-key :campaign_id) s/Str
   (s/optional-key :campaign) s/Str
   (s/optional-key :campaign_type_id) s/Int
   (s/optional-key :campaign_type) s/Str})

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

     (sweet/GET "/performance" []
       :summary "Total performance"
       :query-params [low :- String high :- String]
       :return [Perf]
       (ok (into [] (comp
                     (map (fn [m] (update m :during u/iso-date)))
                     (map coerce-perf))
                 (db/total-perf-by-date db {:customer-id 777309 :low low :high high}))))

     (sweet/GET "/performance/campaign" []
       :summary "Campaign performance"
       :query-params [low :- String high :- String id :- String]
       :return [Perf]
       (ok (into [] (comp
                     (map (fn [m] (update m :during u/iso-date)))
                     (map coerce-perf))
                 (db/cmp-perf-by-id-date db {:customer-id 777309
                                             :id id
                                             :low low
                                             :high high}))))

     (sweet/GET "/performance/type" []
       :summary "Campaign type performance"
       :query-params [low :- String high :- String type :- String]
       :return [Perf]
       (ok (into [] (comp
                     (map (fn [m] (update m :during u/iso-date)))
                     (map coerce-perf))
                 (db/cmp-type-perf db {:customer-id 777309
                                       :type type
                                       :low low
                                       :high high}))))

     ;; Aggregate
     (sweet/GET "/stats/aggregate-segmented" []
       :summary "Forwards to Naver API"
       (ok
        (let [creds (assoc (:naver-creds config)
                           :customer-id 777309)
              ids (->> (naver-adgroup/all creds)
                       (filter #(= "ELIGIBLE" (:status %)))
                       (map :nccAdgroupId))]
          (naver-stats/by-id
           creds
           {:ids ids
            :fields naver-stats/default-fields
            :date-preset :last30days}))))

     ;; Keyword-tool
     (sweet/POST "/keyword-tool" []
       :body [keywords [s/Str]]
       (ok
        (keyword-tool/fp keywords))))))
