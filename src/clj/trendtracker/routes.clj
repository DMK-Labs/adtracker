(ns trendtracker.routes
  (:require [clojure.java.io :as io]
            [clojure.spec.alpha :as s]
            [compojure.api.sweet :as sweet]
            [compojure.route :refer [resources]]
            [ring.util.http-response :refer [ok]]
            [ring.util.response :refer [response]]
            [schema.core :as schema]))

(defn app-routes [endpoint]
  (sweet/routes
   (resources "/")
   (sweet/GET "*" _
     (-> "public/index.html"
         io/resource
         io/input-stream
         response
         (assoc :headers {"Content-Type" "text/html; charset=utf-8"})))))

(defn api-routes [endpoint]
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
       :summary "Customer's stats, filtered by date and id"
       (ok [{:impressions 1574,
             :revenue 57600,
             :clicks 17,
             :id "nkw-a001-01-000000338278388",
             :profit 47073,
             :i2c 0.1282,
             :keyword "핸드폰차량거치대",
             :conversions 2,
             :cost 10527,
             :roi 4.471644343117697,
             :cvr 11.76,
             :cpc 619,
             :ctr 1.09}
            {:impressions 23896,
             :revenue 51600,
             :clicks 41,
             :id "nkw-a001-01-000000338278387",
             :profit 35760,
             :i2c 0.0044,
             :keyword "핸드폰거치대",
             :conversions 1,
             :cost 15840,
             :roi 2.257575757575758,
             :cvr 2.44,
             :cpc 386,
             :ctr 0.18}
            {:impressions 6186,
             :revenue 35800,
             :clicks 49,
             :id "nkw-a001-01-000000338278382",
             :profit 13646,
             :i2c 0.0163,
             :keyword "스마트폰차량용거치대",
             :conversions 1,
             :cost 22154,
             :roi 0.6159610002708309,
             :cvr 2.04,
             :cpc 452,
             :ctr 0.8}
            {:impressions 7594,
             :revenue 66700,
             :clicks 61,
             :id "nkw-a001-01-000000338278787",
             :profit 44425,
             :i2c 0.0266,
             :keyword "자동차핸드폰거치대",
             :conversions 2,
             :cost 22275,
             :roi 1.9943883277216612,
             :cvr 3.28,
             :cpc 365,
             :ctr 0.81}
            {:impressions 3397,
             :revenue 67700,
             :clicks 22,
             :id "nkw-a001-01-000000338278763",
             :profit 60396,
             :i2c 0.0591,
             :keyword "자동차스마트폰거치대",
             :conversions 2,
             :cost 7304,
             :roi 8.268893756845564,
             :cvr 9.09,
             :cpc 332,
             :ctr 0.65}
            {:impressions 31486,
             :revenue 254300,
             :clicks 174,
             :id "nkw-a001-01-000000338278872",
             :profit 181249,
             :i2c 0.0258,
             :keyword "차량용스마트폰거치대",
             :conversions 8,
             :cost 73051,
             :roi 2.481129621771091,
             :cvr 4.6,
             :cpc 420,
             :ctr 0.56}])))))
