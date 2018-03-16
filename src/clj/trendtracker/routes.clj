(ns trendtracker.routes
  (:require [clojure.java.io :as io]
            [compojure.api.sweet :as sweet]
            [compojure.route :refer [resources]]
            [naver-searchad.api.adgroup :as naver-adgroup]
            [naver-searchad.api.campaign :as naver-campaign]
            [naver-searchad.api.adkeyword :as naver-keyword]
            [naver-searchad.api.stats :as naver-stats]
            [ring.util.http-response :as respond]
            [ring.util.response :refer [response]]
            [schema.coerce :as coerce]
            [schema.core :as s]
            [trendtracker.modules.keyword-tool :as keyword-tool]
            [trendtracker.config :refer [config creds]]
            [trendtracker.db :as db]
            [trendtracker.models.daily-stats :as daily-stats]
            [trendtracker.models.keywords :as keywords]
            [trendtracker.models.optimize :as optimize]
            [trendtracker.models.portfolio :as portfolio]
            [trendtracker.models.user :as user]
            [trendtracker.models.segments :as segments]
            [trendtracker.modules.auth :as auth]
            [trendtracker.utils :as u]
            [trendtracker.modules.landscape :as landscape]
            [huri.core :as h]
            [clojure.set :as set]))

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
   (s/optional-key :cpm) s/Any
   (s/optional-key :cpa) s/Any
   (s/optional-key :cpc) s/Any
   (s/optional-key :i2c) Double
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
       (respond/ok {:result (+ x y)}))

     (sweet/GET "/performance" []
       :summary "Total performance"
       :query-params [low :- String high :- String customer-id :- s/Int]
       (respond/ok
        (into [] (comp
                  (map (fn [m] (update m :during u/iso-date)))
                  (map coerce-perf)
                  (map daily-stats/add-ratios2))
              (daily-stats/by-customer
               db
               {:customer-id customer-id
                :low low
                :high high}))))

     (sweet/GET "/performance/type" []
       :summary "Campaign type performance"
       :query-params [low :- s/Str high :- s/Str type :- s/Str customer-id :- s/Int]
       (respond/ok
        (into [] (comp
                  (map (fn [m] (update m :during u/iso-date)))
                  (map coerce-perf)
                  (map daily-stats/add-ratios2))
              (daily-stats/by-type
               db
               {:customer-id customer-id
                :campaign-type type
                :low low
                :high high}))))

     (sweet/GET "/performance/campaign" []
       :summary "Campaign performance"
       :query-params [low :- s/Str
                      high :- s/Str
                      campaign-id :- s/Str
                      customer-id :- s/Int]
       (respond/ok
        (into [] (comp
                  (map (fn [m] (update m :during u/iso-date)))
                  (map coerce-perf)
                  (map daily-stats/add-ratios2))
              (daily-stats/by-campaign
               db
               {:customer-id customer-id
                :campaign-id campaign-id
                :low low
                :high high}))))

     (sweet/GET "/performance/adgroup" []
       :summary "Adgroup performance"
       :query-params [low :- String high :- String id :- String customer-id :- s/Int]
       (respond/ok
        (into [] (comp
                  (map (fn [m] (update m :during u/iso-date)))
                  (map coerce-perf)
                  (map daily-stats/add-ratios2))
              (daily-stats/by-adgroup
               db
               {:customer-id customer-id
                :adgroup-id id
                :low low
                :high high}))))

     ;;* Aggregate
     (sweet/GET "/stats/segmented" []
       :query-params [low :- s/Str high :- s/Str customer-id :- s/Int type :- s/Str]
       (respond/ok
        (let [creds (creds customer-id)
              [k segments] (case type
                             "adgroup" [:nccAdgroupId (naver-adgroup/all creds)]
                             "campaign" [:nccCampaignId (naver-campaign/all creds)])
              ids (map k segments)
              stats (set/join
                     segments
                     (naver-stats/by-id
                      creds
                      {:ids ids
                       :fields (conj naver-stats/default-fields :avgRnk)
                       :time-range {:since low :until high}})
                     {k :id})]
          (sort-by :profit
                   >
                   (transduce (comp
                               (map #(assoc % :profit (- (:convAmt %) (:salesAmt %))))
                               (map #(assoc % :roas
                                              (if (zero? (:salesAmt %))
                                                0
                                                (/ (:convAmt %) (:salesAmt %))))))
                              conj
                              stats)))))

     (sweet/GET "/stats/keywords" []
       :query-params [low :- s/Str high :- s/Str customer-id :- s/Int]
       (respond/ok
        (map daily-stats/add-ratios2
             (segments/keywords db {:low low :high high :customer-id customer-id}))))

     (sweet/GET "/segments/pc-mobile" []
       :query-params [low :- s/Str high :- s/Str customer-id :- s/Int]
       (respond/ok
        (into [] (comp
                  (map #(update % :pc_mobile_type (fn [s] (case s
                                                            "P" "PC"
                                                            "M" "Mobile"))))
                  (map daily-stats/add-ratios2))
              (segments/pc-mobile
               db
               {:customer-id customer-id
                :low low
                :high high}))))

     ;;* Keyword-tool
     (sweet/POST "/keyword-tool" []
       :return s/Any
       :body-params [keywords :- s/Any
                     include-related? :- s/Any]
       (respond/ok
        (keyword-tool/simple-process keywords include-related?)))

     ;;* Users
     (sweet/POST "/login" []
       :return s/Any
       :body-params [email password]
       (if-let [logged-in-info-map (auth/logged-in-info email password)]
         (respond/ok logged-in-info-map)
         (respond/unauthorized {})))

     (sweet/GET "/user" []
       :header-params [authorization :- String]
       (respond/ok (auth/unsign-auth-header authorization)))

     (sweet/GET "/access-rights" []
       (respond/ok (user/access-rights db {})))

     ;;** Portfolio
     (sweet/GET "/portfolio" []
       :query-params [customer-id :- s/Int]
       (respond/ok (portfolio/tree customer-id)))

     (sweet/GET "/portfolio/optimizing" []
       :query-params [customer-id :- s/Int]
       (respond/ok (portfolio/optimizing customer-id)))

     ;;** Optimize
     (sweet/GET "/optimize/settings" []
       :query-params [customer-id :- s/Int]
       (respond/ok
        (if-let [s (optimize/settings customer-id)]
          s
          :no-settings)))

     (sweet/PUT "/optimize/settings" []
       :body-params [customer-id :- s/Int
                     budget :- s/Int
                     objective :- s/Str
                     targets :- s/Str
                     bid-limit :- s/Int]
       (respond/ok
        (optimize/save-settings customer-id budget objective targets bid-limit)))

     (sweet/GET "/optimize/marginals" []
       :query-params [customer-id :- s/Int]
       (respond/ok (optimize/fetch-marginals customer-id)))

     (sweet/GET "/optimize/ridgeline" []
       :query-params [customer-id :- s/Int]
       (respond/ok (landscape/ridgeline (optimize/fetch-marginals customer-id))))

     (sweet/GET "/optimize/detail" []
       :query-params [customer-id :- s/Int
                      budget :- s/Int]
       (respond/ok
        (h/select-cols
         [:keyword-id :bid :impressions :clicks :cost]
         (landscape/detail (optimize/fetch-marginals customer-id) budget))))

     ;;** Bids according to Naver
     (sweet/GET "/bids/with-minimum-exposure" []
       :query-params [customer-id :- s/Int
                      budget :- s/Int]
       (respond/ok
        (landscape/optimized-bids-with-minimums customer-id budget)))

     (sweet/GET "/bids/with-min-70" []
       :query-params [customer-id :- s/Int
                      budget :- s/Int]
       (respond/ok
        (landscape/optimized-bids-with-min-70 customer-id budget)))

     ;;** Keywords
     (sweet/GET "/keywords/all" []
       :query-params [customer-id :- s/Int]
       (respond/ok (keywords/all db {:customer-id customer-id}))))))

