(ns trendtracker.datasources
  (:require [keechma.toolbox.ajax :as ajax]
            [keechma.toolbox.dataloader.subscriptions :refer [map-loader]]
            [hodgepodge.core :refer [get-item local-storage]]
            [promesa.core :as p]
            [trendtracker.utils :as u]
            [cljs.core.match :refer-macros [match]]
            [trendtracker.api :as api]))

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
   :loader (map-loader #(get-item local-storage "lacuna-jwt-token"))
   :params (fn [prev _ _] (when (:data prev) ignore-datasource-check))})

(def current-user-datasource
  {:target [:edb/named-item :user/current]
   :loader api-loader
   :deps   [:jwt]
   :params (fn [prev _ {:keys [jwt]}]
             (when jwt
               (if (:data prev)
                 ignore-datasource-check
                 {:url     "/user"
                  :headers (auth-header jwt)})))})

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
   :params (fn [prev {:keys [client]} {:keys [managed-clients]}]
             (if-let [data (:data prev)]
               data
               (if client
                 (first (filter #(= client (str (:customer_id %))) managed-clients))
                 (first managed-clients))))
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
             (when (and (= (:page route) "keywords") current-client)
               {:url "/keywords/all"
                :customer-id (:customer_id current-client)}))})

(def portfolio-optimizing-datasource
  {:target [:kv :portfolio-optimizing]
   :deps [:current-client]
   :loader api-loader
   :params (fn [_ route {:keys [current-client]}]
             (when current-client
               {:url "/portfolio/optimizing"
                :customer-id (:customer_id current-client)}))})

(def optimize-settings-datasource
  {:target [:kv :optimize :settings]
   :params (fn [prev _ _] (:data prev))
   :loader pass-through-params})

(def marginals-datasource
  {:target [:kv :optimize :marginals]
   :deps [:optimize-settings :current-client]
   :params (fn [_ route {:keys [optimize-settings current-client]}]
             (when (and current-client optimize-settings (and (= (:page route) "optimize")
                                                              (= (:subpage route) "new")))
               {:url "/optimize/marginals"
                :customer-id (:customer_id current-client)
                :objective (:objective optimize-settings)}))
   :loader api-loader})

(defn fit-to-budget
  "Takes marginal segments from the total landscape pool until budget is
  exhausted."
  [budget marginals]
  (loop [todo marginals
         acc []]
    (if (or (empty? todo)
            (> (+ (:marginal-cost (first todo)) (u/sum :marginal-cost acc)) budget))
      acc
      (recur (next todo)
             (conj acc (first todo))))))

(defn stats [df cost]
  (let [res (fit-to-budget cost df)
        clicks (u/sum :marginal-clicks res)
        cost (u/sum :marginal-cost res)
        impressions (u/sum :marginal-impressions res)]
    {:clicks clicks
     :cost cost
     :impressions impressions
     :cpc (double (/ cost clicks))
     :cpm (* 1000. (/ cost impressions))
     ;; :res (model/final-bid-perf-estimates res)
     }))

(def optimize-stats-datasource
  {:target [:kv :optimize :stats]
   :deps [:optimize-settings :marginals]
   :params (fn [_ route {:keys [optimize-settings marginals]}]
             (when (= (:page route) "optimize")
               (stats marginals (:budget optimize-settings))))
   :loader pass-through-params})

(def daily-stats-datasource
  "Stats depend on the date-range, so it will be reloaded whenever date-range
  changes."
  {:target [:kv :daily-stats]
   :deps   [:jwt :date-range :cascader :current-client]
   :params (fn [_ {:keys [page]} deps]
             (;; when-not (or (= "login" page)
              ;;              (= "logout" page))
              when (= "dashboard" page)
              (select-keys deps [:date-range :cascader :current-client :jwt])))
   :loader (map-loader
            (fn [req]
              (let [range       (get-in req [:params :date-range])
                    casc        (get-in req [:params :cascader])
                    jwt         (get-in req [:params :jwt])
                    customer-id (get-in req [:params :current-client :customer_id])]
                (when (and (seq range) (seq casc) jwt)
                  (match casc
                    ["total"] (api/total-perf jwt customer-id range)
                    [type] (api/campaign-type-perf jwt customer-id type range)
                    [type cmp-id] (api/campaign-perf jwt customer-id cmp-id range))))))})

(def date-range-datasource
  {:target [:kv :date-range]
   :params (fn [prev _ _] (:data prev))
   :loader pass-through-params})

(def cascader-datasource
  {:target [:kv :cascader]
   :params (fn [prev _ _] (:data prev))
   :loader pass-through-params})

(def datasources
  {:jwt                  jwt-datasource
   :current-user         current-user-datasource
   :current-client       current-client-datasource
   :managed-clients      managed-clients-datasource
   :date-range           date-range-datasource
   :cascader             cascader-datasource
   :daily-stats          daily-stats-datasource
   :portfolio            portfolio-datasource
   :portfolio-optimizing portfolio-optimizing-datasource
   :registered-keywords  registered-keywords-datasource

   :marginals            marginals-datasource
   :optimize-settings    optimize-settings-datasource
   :optimize-stats       optimize-stats-datasource})
