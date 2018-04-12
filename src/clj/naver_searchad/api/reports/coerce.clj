(ns naver-searchad.api.reports.coerce
  (:require [clojure.instant :as instant]
            [com.rpl.specter :refer :all]
            [huri.core :as h]
            [naver-searchad.date-utils :as date-utils]
            [semantic-csv.core :as scsv :refer [->int]]))

;;* Column value coercions
(def ->pc-mobile-type {"P" "pc" "M" "mobile"})

(def ->owner-type {"0" :owner
                   "1" :ad-manager
                   "2" :analyzer
                   "3" :tax-manager
                   "4" :tax-analyzer
                   "5" :na-manager
                   "6" :na-analyzer
                   "-1" :disabled
                   "-2" :penalty})

(def ->link-status {"10" :request
                    "20" :authorize
                    "40" :reject
                    "50" :cancel})

(def ->campaign-type {"1" :powerlink
                      "2" :shopping
                      "4" :brand})

(def ->content-type {"INFORMATION" :information "PRODUCT" :product})

(def ->delivery-method {"1" :accelerated "2" :standard})

(def on-off {"0" false "1" true})

(def ->inspect-status {"10" :under-review
                       "20" :approved
                       "30" :limited-approved
                       "40" :pending})

(def ->ad-extension-type {"1" :phone
                          "2" :address
                          "3" :naver-booking
                          "4" :naver-talk})

(def ->business-channel-type {"1" :web-site
                              "2" :phone
                              "3" :address
                              "4" :naver-booking
                              "5" :naver-talk
                              "6" :naver-shoppping
                              "7" :naver-blog})

(defn ->instant-timestamp [i]
  (when (string? i)
    (instant/read-instant-timestamp i)))

(defn bool-str->int [s]
  (cond
    (= "false" s) 0
    (= "true" s) 1))

(defn if-int->int [s]
  (if (every? #{\0 \1 \2 \3 \4 \5 \6 \7 \8 \9} s)
    (Integer/parseInt s)
    s))

;;* Column coercion map
(def column->type
  {:id if-int->int
   :bid ->int
   :clicks ->int
   :cost ->int
   :impressions ->int
   :conversions ->int
   :conversion_revenue ->int
   :ad_rank_sum ->int
   :quality_index ->int
   :ad_group_bid ->int
   :contents_network_bid ->int
   :keyword_bid ->int
   :daily_budget ->int
   :pc_network_bid_weight ->int
   :mobile_network_bid_weight ->int
   :keyword_plus_bid_weight ->int
   :customer_id ->int
   :event_id ->int
   :super_event_id ->int

   :keyword_inspect_status_id ->int
   :ad_inspect_status_id ->int
   :ad_extension_inspect_status ->int
   :pc_inspect_status_id ->int
   :mobile_inspect_status_id ->int
   :inspect_status_id ->int
   :ad_extension_type_id ->int
   :campaign_type_id ->int
   :delivery_method_id ->int
   :link_status_id ->int
   :owner_type_id ->int

   :is_using_period on-off
   :is_using_ad_group_bid (fn [s]
                            (let [x (clojure.edn/read-string s)]
                              (if (boolean? x)
                                x
                                (case x
                                  1 true
                                  0 false))))
   :is_off on-off
   :is_using_contents_network_bid on-off
   :is_using_daily_budget on-off
   :keyword_plus on-off
   :is_ad_link_enabled on-off

   :business_channel_type_id ->int
   ;; :content_type ->content-type

   ;; :pc_mobile_type ->pc-mobile-type
   :method_id ->int
   :type_id ->int

   ;; :region_code target/->region

   :business_id ->int
   :super_business_id ->int
   :level ->int

   ;; :period_starts_at ->instant-timestamp
   ;; :period_ends_at ->instant-timestamp
   ;; :del_at ->instant-timestamp
   ;; :reg_at ->instant-timestamp
   ;; :upd_at ->instant-timestamp

   ;; Conversion 
   :media_id ->int
   :count ->int
   :revenue ->int
   :during #(date-utils/parse-yyyymmdd % true)})

(def date-cols
#{:period_starts_at :period_ends_at :del_at :reg_at :upd_at :contact_ends_at :contract_revoked_at})

(defn empty-strings-are-nil
  "For use when coercing return JSON of API calls. An empty string \"\" or filler
  string \"-\" coerce to `nil`."
  [x]
  (if (string? x)
    (when-not (= "" x)
      ;; (or (= "" x) (= "-" x))
      x)
    x))

(defn nillify-empty-string-vals
  [ms]
  (transform [ALL MAP-VALS] empty-strings-are-nil ms))

(defn parse-dates [ms]
  (transform [ALL ALL (comp date-cols first) LAST] ->instant-timestamp ms))

(defn edn-vals
  "Takes a semantic csv map, casts the type of each column according to
  `column->type` map."
  [job-dump]
  (->> job-dump
       (scsv/cast-with
        (select-keys column->type (h/cols job-dump)))))
