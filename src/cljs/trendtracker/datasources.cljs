(ns trendtracker.datasources
  (:require [keechma.toolbox.dataloader.subscriptions :refer [map-loader]]
            [hodgepodge.core :refer [get-item local-storage]]
            [cljs.core.match :refer-macros [match]]
            [trendtracker.api :as api]
            [trendtracker.utils :as u]
            [goog.net.cookies :as cookies]))

(def ignore-datasource-check :keechma.toolbox.dataloader.core/ignore)

(def api-loader
  (map-loader
   (fn [req]
     (when-let [params (:params req)]
       (api/dataloader-req params)))))

(def pass-through-params
  (map-loader
   (fn [req] (:params req))))

(defn auth-header
  ([jwt] (auth-header {} jwt))
  ([headers jwt]
   (if jwt
     (assoc headers :authorization (str "Bearer " jwt))
     headers)))

(def jwt-datasource
  {:target [:kv :jwt]
   :loader (map-loader #(get-item local-storage "trendtracker-jwt-token"))
   :params (fn [prev _ _] (when (:data prev) ignore-datasource-check))})

(def session-id-datasource
  {:target [:kv :session-id]
   :loader (map-loader (fn [_] (.get goog.net.cookies "tracker_session_id")))
   :params (fn [prev _ _] (when (:data prev) ignore-datasource-check))})

(def session-valid-datasource
  {:target [:kv :session/current]
   :deps [:session-id]
   :params (fn [_ _ {:keys [session-id]}]
             (when session-id
               {:session-id session-id}))
   :loader (map-loader
            (fn [req]
              (when-let [params (:params req)]
               (api/session-logged-in? (:session-id params)))))})

(def current-user-datasource
  {:target [:edb/named-item :user/current]
   :deps [:session-valid]
   :params (fn [_ _ {:keys [session-valid]}]
             (when session-valid
               {:valid-session true}))
   :loader (map-loader
            (fn [req]
              (when (:valid-session (:params req))
                {:id 3 :email "dmk@datamarketing.co.kr" :name "DMK" :tenant "Data Marketing Korea" :naver_id
                 719425})))})

(def managed-clients-datasource
  {:target [:edb/collection :managed-clients/list]
   :deps [:jwt :current-user]
   :params (fn [_ _ {:keys [jwt current-user]}]
             (when current-user
               (-> {:url "/access-rights"}
                   (assoc :headers (auth-header jwt)))))
   :loader api-loader})

(def current-client-datasource
  {:target [:kv :current-client]
   :deps [:managed-clients]
   :params (fn [_ {:keys [client]} {:keys [managed-clients]}]
             (if client
               (first (filter #(= client (str (:customer_id %))) managed-clients))
               (first managed-clients)))
   :loader pass-through-params})

(def portfolio-datasource
  {:target [:kv :portfolio]
   :deps [:current-client]
   :loader api-loader
   :params (fn [_ _ {:keys [current-client]}]
             (when current-client
               {:url "/portfolio"
                :customer-id (:customer_id current-client)}))})

(def registered-keywords-datasource
  {:target [:kv :keywords]
   :deps [:current-client]
   :loader api-loader
   :params (fn [_ route {:keys [current-client]}]
             (when (and (= "keyword-tool" (:page route)) current-client)
               {:url "/keywords/all"
                :customer-id (:customer_id current-client)}))})

(def portfolio-optimizing-datasource
  {:target [:kv :portfolio-optimizing]
   :deps [:current-client]
   :loader api-loader
   :params (fn [_ _ {:keys [current-client]}]
             (when current-client
               {:url "/portfolio/optimizing"
                :customer-id (:customer_id current-client)}))})

(def optimize-settings-datasource
  {:target [:kv :optimize :settings]
   :deps [:current-client]
   :params (fn [_ _ {:keys [current-client]}]
             (when current-client
               {:url "/optimize/settings"
                :customer-id (:customer_id current-client)}))
   :loader api-loader})

(def ridgeline-datasource
  {:target [:kv :optimize :ridgeline]
   :deps [:current-client]
   :params (fn [_ route {:keys [current-client]}]
             (when (and current-client
                        (= (:page route) "optimize")
                        (= (:subpage route) "settings"))
               {:url "/optimize/ridgeline"
                :customer-id (:customer_id current-client)}))
   :loader api-loader})

(def optimize-stats-datasource
  {:target [:kv :optimize :stats]
   :deps [:optimize-settings :ridgeline]
   :params (fn [_ route {:keys [optimize-settings ridgeline]}]
             (when (= (:page route) "optimize")
               (->> ridgeline
                    (drop-while #(< (:cost %) (:budget optimize-settings)))
                    first)))
   :loader pass-through-params})

(def optimize-detail-datasource
  {:target [:kv :optimize :detail]
   :deps [:current-client :optimize-settings]
   :params (fn [_ route {:keys [optimize-settings current-client]}]
             (when (and (= (:page route) "optimize")
                        (:modal route))
               {:url "/optimize/detail"
                :customer-id (:customer_id current-client)
                :budget (:budget optimize-settings)}))
   :loader api-loader})

(def segment-stats-datasource
  {:target [:kv :segment-stats]
   :deps [:current-client :date-range]
   :params (fn [_ route {:keys [current-client date-range]}]
             (let [segment (:seg route)]
               (when (and current-client date-range
                          (#{"dashboard" "insights"} (:page route)))
                 (merge (case segment
                          "keyword" (if-let [id (:adgrp route)]
                                      {:url "/stats/aggregate/by-adgroup"
                                       :id id}
                                      {:url "/stats/keywords"
                                       :customer-id (:customer_id current-client)})
                          {:url "/stats/aggregate/adgroups"
                           :customer-id (:customer_id current-client)
                           :type (or segment "adgroup")})
                        (u/parse-date-range (:curr date-range))))))
   :loader api-loader})

(def with-min-70-bids-datasource
  {:target [:kv :naver :with-min-70-bids]
   :deps [:current-client :optimize-settings]
   :params (fn [_ route {:keys [current-client optimize-settings]}]
             (when (and (= (:page route) "optimize")
                        (:modal route))
               {:url "/bids/with-min-70"
                :customer-id (:customer_id current-client)
                :budget (:budget optimize-settings)}))
   :loader api-loader})

(def with-minimum-exposure-bids-datasource
  {:target [:kv :naver :with-minimum-exposure-bids]
   :deps [:current-client :optimize-settings]
   :params (fn [_ route {:keys [current-client optimize-settings]}]
             (when (and (= (:page route) "optimize")
                        (:modal route))
               {:url "/bids/with-minimum-exposure"
                :customer-id (:customer_id current-client)
                :budget (:budget optimize-settings)}))
   :loader api-loader})

(def creatives-datasource
  {:target [:kv :creatives]
   :deps [:current-client :date-range]
   :params (fn [_ {page :page} {:keys [current-client date-range]}]
             (when (and current-client date-range (= "creatives" page))
               (merge {:url "/stats/ad-creatives"
                       :customer-id (:customer_id current-client)}
                      (u/parse-date-range (:curr date-range)))))
   :loader api-loader})

(def keywords-datasource
  {:target [:kv :keywords]
   :deps [:current-client :date-range]
   :params (fn [_ {page :page} {:keys [current-client date-range]}]
             (when (and current-client date-range (= "keywords" page))
               (merge {:url "/stats/keywords"
                       :customer-id (:customer_id current-client)}
                      (u/parse-date-range (:curr date-range)))))
   :loader api-loader})

(def pc-mobile-split-datasource
  {:target [:kv :segments :pc-mobile]
   :deps [:date-range :current-client]
   :params (fn [_ {page :page} {:keys [date-range current-client]}]
             (when (and (= "dashboard" page) (seq date-range))
               (let [r (assoc (u/parse-date-range (:curr date-range))
                         :url "/segments/pc-mobile"
                         :customer-id (:customer_id current-client))]
                 r)))
   :loader api-loader})

(def daily-stats-datasource
  "Stats depend on the date-range, so it will be reloaded whenever date-range
  changes."
  {:target [:kv :daily-stats]
   :deps [:jwt :date-range :cascader :current-client]
   :params (fn [_ {page :page} deps]
             (when (= "dashboard" page)
               (select-keys deps [:date-range :cascader :current-client :jwt])))
   :loader (map-loader
            (fn [req]
              (let [range (get-in req [:params :date-range])
                    casc (get-in req [:params :cascader])
                    jwt (get-in req [:params :jwt])
                    customer-id (get-in req [:params :current-client :customer_id])]
                (when (and (seq range) (seq casc) jwt)
                  (match casc
                    ["total"] (api/total-perf jwt customer-id range)
                    [type] (api/campaign-type-perf jwt customer-id type range)
                    [_ cmp-id] (api/campaign-perf jwt customer-id cmp-id range)
                    [_ _ adgrp-id] (api/adgroup-perf jwt customer-id adgrp-id range))))))})

(def date-range-datasource
  {:target [:kv :date-range]
   :params (fn [prev _ _] (:data prev))
   :loader pass-through-params})

(def first-recorded-performance-datasource
  {:target [:kv :first-recorded-performance]
   :deps [:current-client]
   :params (fn [_ _ {:keys [current-client]}]
             (when current-client
               {:url "/performance/first"
                :customer-id (:customer_id current-client)}))
   :loader api-loader})

(def cascader-datasource
  {:target [:kv :cascader]
   :params (fn [prev _ _] (:data prev))
   :loader pass-through-params})

(def datasources
  {:jwt jwt-datasource
   :session-id session-id-datasource
   :session-valid session-valid-datasource
   :current-user current-user-datasource
   :current-client current-client-datasource
   :managed-clients managed-clients-datasource
   :date-range date-range-datasource
   :cascader cascader-datasource
   :daily-stats daily-stats-datasource
   :portfolio portfolio-datasource
   :portfolio-optimizing portfolio-optimizing-datasource
   :registered-keywords registered-keywords-datasource
   :ridgeline ridgeline-datasource
   :optimize-settings optimize-settings-datasource
   :optimize-stats optimize-stats-datasource
   :optimize-detail optimize-detail-datasource
   :with-minimum-exposure-bids with-minimum-exposure-bids-datasource
   :with-min-70-bids with-min-70-bids-datasource
   :segment-stats segment-stats-datasource
   :pc-mobile-split pc-mobile-split-datasource
   :creatives creatives-datasource
   :keywords keywords-datasource
   :first-recorded-performance first-recorded-performance-datasource})
