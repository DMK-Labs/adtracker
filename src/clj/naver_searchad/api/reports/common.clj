(ns naver-searchad.api.reports.common
  (:require [byte-streams :as byte-streams]
            [clojure.data.csv :as csv]
            [clojure.string :as string]
            [naver-searchad.api.reports.coerce :as reports.coerce]
            [naver-searchad.api.request :as request]
            [semantic-csv.core :as scsv]))

;;* Stat report specs
(def stats-header
  "Each reportTp mapped to a vector of its header columns.

  \"AD\" -> [:date :customer_id ...]"
  {"AD" [:during :customer_id :campaign_id :ad_group_id :keyword_id :ad_id
         :business_channel_id :media_id :pc_mobile_type :impressions :clicks :cost
         :ad_rank_sum]
   "AD_DETAIL" [:during :customer_id :campaign_id :ad_group_id :keyword_id
                :ad_id :business_channel_id :hours :region_code :media_id
                :pc_mobile_type :impressions :clicks :cost :ad_rank_sum]
   "ADEXTENSION" [:during :customer_id :campaign_id :ad_group_id :keyword_id
                  :ad_id :ad_extension_id :ad_ext_business_channel_id :media_id
                  :pc_mobile_type :impressions :clicks :cost :ad_rank_sum]
   "EXPKEYWORD" [:during :customer_id :campaign_id :ad_group_id :search_keyword
                 :media_id :pc_mobile_type :impressions :clicks :cost]
   "AD_CONVERSION" [:during :customer_id :campaign_id :ad_group_id :keyword_id :ad_id
                    :business_channel_id :media_id :pc_mobile_type :method_id
                    :type_id :count :revenue]
   "AD_CONVERSION_DETAIL" [:during :customer_id :campaign_id :ad_group_id :keyword_id
                           :ad_id :business_channel_id :hours :region_id :media_id
                           :pc_mobile_type :method :type :count :revenue]
   "ADEXTENSION_CONVERSION" [:during :customer_id :campaign_id :ad_group_id :keyword_id
                             :ad_id :ad_extension_id :ad_ext_business_channel_id :media_id
                             :pc_mobile_type :method_id :type_id :count :revenue]
   "NAVERPAY_CONVERSION" [:during :customer_id :campaign_id :ad_group_id :keyword_id
                          :ad_id :media_id :pc_mobile_type :count :revenue]})

(def master-header
  {"Account" [:customer_id :login_id :company_name :link_status_id :owner_type_id :reg_at]
   "Campaign" [:customer_id :id :name :campaign_type_id :delivery_method_id
               :is_using_period :period_starts_at :period_ends_at :reg_at :del_at]
   "CampaignBudget" [:customer_id :campaign_id :is_using_daily_budget :daily_budget
                     :reg_at :del_at]
   "BusinessChannel" [:customer_id :name :id :business_channel_type_id
                      :channel_contents :pc_inspect_status_id :mobile_inspect_status_id
                      :reg_at :del_at]
   "Adgroup" [:customer_id :id :campaign_id :name :bid :is_off
              :is_using_contents_network_bid :contents_network_bid :pc_network_bid_weight
              :mobile_network_bid_weight :keyword_plus :keyword_plus_bid_weight
              :pc_business_channel_id :mobile_business_channel_id :reg_at :del_at :content_type]
   "AdgroupBudget" [:customer_id :ad_group_id :is_using_daily_budget :daily_budget :reg_at :del_at]
   "Keyword" [:customer_id :ad_group_id :id :keyword :bid
              :pc_landing_url :mobile_landing_url :is_off :inspect_status_id
              :is_using_ad_group_bid :reg_at :del_at]
   "Ad" [:customer_id :ad_group_id :id :inspect_status_id :subject
         :description :pc_landing_url :mobile_landing_url :is_off :reg_at :del_at]
   "AdExtension" [:customer_id :id :ad_extension_type_id :owner_id
                  :pc_business_channel_id :mobile_business_channel_id
                  :monday_target_time :tuesday_target_time :wednesday_target_time
                  :thursday_target_time :friday_target_time :saturday_target_time
                  :sunday_target_time :is_off :inspect_status_id :reg_at :del_at]
   "Qi" [:customer_id :ad_group_id :keyword_id :keyword :quality_index]
   "Label" [:customer_id :id :name :reg_at :upd_at]
   "LabelRef" [:customer_id :label_id :reference_id :reg_at :upd_at]
   "Media" [:type :id :name :url :is_in_naver_ad_networks
            :is_portal_site :is_for_pc :is_for_mobile :is_in_search_ad_networks
            :is_in_contents_ad_networks :group_id :contact_ends_at
            :contract_revoked_at]
   "Biz" [:id :name :super_business_id :level]
   "SeasonalEvent" [:id :name :super_event_id :level :relevant_months]
   "ShoppingProduct" [:customer_id :ad_group_id :id :inspect_status_id :is_off
                      :ad_product_name :ad_image_url :bid :is_using_ad_group_bid :is_ad_link_enabled
                      :reg_at :del_at :product_id :mall_product_id :product_name :product_image_url
                      :pc_landing_url :mobile_landing_url :pc_price :mobile_price :delivery_fee
                      :naver_shopping_category_1 :naver_shopping_category_2
                      :naver_shopping_category_3 :naver_shopping_category_4
                      :naver_shopping_category_id_1 :naver_shopping_category_id_2
                      :naver_shopping_category_id_3 :naver_shopping_category_id_4
                      :mall_category_name]
   "ContentsAd" [:customer_id :ad_group_id :id :inspect_status_id
                 :subject :description :pc_landing_url :mobile_landing_url :image_url
                 :company_name :issue_on :release_on :is_off :reg_at :del_at]})

;;* Util fns

(defn parse-authtoken
  "Drops prepending uri and query param declaration leaving just the auth token
  string."
  [download-url]
  (string/replace-first
   download-url
   #"https://api.naver.com/report-download\?authtoken="
   ""))

(def run-tries (atom 0))
(defn run-when-built
  "ACTION is a two-arity function taking the Naver API credentials and a
  job map.

  Checks to see if the report is built every 1s. If built, runs ACTION on the
  report, i.e. downloading or fetching as edn. If job is not built after 20s,
  aborts run attempt."
  [creds report-fetcher id action]
  (let [job (report-fetcher creds id)]
    (case (:status job)
      "BUILT" (action creds job)
      "NONE" (reset! run-tries 0)
      :else (if (> @run-tries 20)
              (reset! run-tries 0)
              (do
                (println "Report unfinished--waiting to check again...")
                (Thread/sleep 1000)
                (println "Checking again:" (swap! run-tries inc) "tries...")
                (run-when-built creds report-fetcher id action))))))

(defmulti report-header
  "Dispatches on report type by checking if certain keys exist. Complected to keys
  of Naver API response map."
  (fn [report]
    (cond
      (:item report) :master-report
      (:reportTp report) :stat-report)))

(defmethod report-header :stat-report [job]
  (stats-header (:reportTp job)))

(defmethod report-header :master-report [job]
  (master-header (:item job)))

(defn download
  [creds {:keys [downloadUrl] :as job}]
  (-> @(request/raw creds :get "/report-download"
                    {:query-params {:authtoken (parse-authtoken downloadUrl)}
                     :body ""})
      :body
      byte-streams/to-reader))

(defn as-csv
  [creds {:keys [downloadUrl] :as job}]
  (csv/read-csv (download creds job) :separator \tab))

(defn as-edn
  "Dowloads the contents of a report (tsv), converts to edn while coercing value
  types as appropriate and returns as data."
  [creds {:keys [downloadUrl] :as job}]
  (->> (as-csv creds job)
       (scsv/mappify {:header (report-header job)})
       reports.coerce/nillify-empty-string-vals
       reports.coerce/parse-dates
       reports.coerce/edn-vals))
