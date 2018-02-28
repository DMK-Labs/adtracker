(ns trendtracker.ui.components.optimize.detail
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [route> sub>]]
            [antizer.reagent :as ant]
            [goog.string :as gstring]
            [trendtracker.helpers.download :as download]
            [trendtracker.utils :as u]))

(def columns
  [{:title "광고 구분" :key "Type"}
   {:title "캠페인" :key "Campaign"}
   {:title "캠페인 ID" :key "Campaign ID"}
   {:title "광고그룹" :key "Adgroup"}
   {:title "광고그룹 ID" :key "Adgroup ID"}
   {:title "최적화 전" :key "previous"
    :children
    [{:title "노출" :key "노출"}
     {:title "클릭" :key "클릭"}
     {:title "비용" :key "비용"}
     {:title "순위(평균)" :key "순위(평균)"}
     {:title "전환" :key "전환"}
     {:title "매출" :key "매출"}]}
   {:title "최적화 후 (예측)"
    :key "optimized"
    :children
    [{:title "노출" :key "opt노출"}
     {:title "클릭" :key "opt클릭"}
     {:title "비용" :key "opt비용"}
     {:title "순위(평균)" :key "opt순위(평균)"}
     {:title "전환" :key "opt전환"}
     {:title "매출" :key "opt매출"}]}])

(def bid-columns
  [{:title "Keyword ID" :key "keyword-id" :dataIndex :keyword-id}
   {:title "키워드" :key "keyword" :dataIndex :keyword}
   {:title "Bid" :key "bid" :dataIndex :bid :sorter (u/sorter-by :bid)}
   {:title "Impressions" :key "impressions" :dataIndex :impressions}
   {:title "Clicks" :key "clicks" :dataIndex :clicks}
   {:title "비용" :key "cost" :dataIndex :cost}])

(defn request-optimization [ctx _]
  (do
    (ui/redirect ctx {:page "optimize"
                      :client (:client (route> ctx))})
    (js/setTimeout
     (fn []
       (ant/notification-success
        {:message "최적화 실시"
         :description "AdTracker는 고객님의 입찰 광고 캠페인의 효율을 극대화 하기 위한 최적화를 실시하고 있습니다."
         :placement "bottomRight"}))
     300)))

(defn render [ctx]
  (let [budget (:budget (sub> ctx :optimize-settings))
        client (:login_id (sub> ctx :current-client))
        route (route> ctx)
        guarantee-min (:guarantee-min route)
        ;; detail (sub> ctx :optimize-detail)
        min-exposure-bids (sub> ctx :with-minimum-exposure-bids)
        min-70-bids (sub> ctx :with-min-70-bids)
        data (if guarantee-min min-exposure-bids min-70-bids)]
    [:div
     [ant/row {:type "flex" :justify "space-between" :gutter 16 :align "middle"}
      [ant/col
       [:div
        "최저노출 보장: "
        [ant/switch {:checked (boolean (:guarantee-min (route> ctx)))
                     :onClick #(ui/redirect
                                ctx
                                (if guarantee-min
                                  (dissoc route :guarantee-min)
                                  (assoc route :guarantee-min true)))}]]]
      [ant/col
       [ant/button
        {:on-click #(download/download-csv
                     {:filename (str client "입찰표_" budget "예산.csv")
                      :header [:adgroup-id :keyword-id :keyword :bid]
                      :content (concat [nil nil nil nil
                                        ["[Important] Input values from the line 7 will be reflected to the system. Do not delete lines from 1 to 6."]
                                        ["Ad Group ID" "Keyword ID" "Keyword" "Keyword Bid"]]
                                       data)
                      :prepend-header false})
         :disabled (empty? data)
         :icon "download"}
        "대량관리 CSV 내려 받기"]]]
     [ant/spin {:spinning (empty? data)}
      [ant/row {:style {:margin-top 16}}
       [ant/table
        {;; :scroll {:x 1500}
         :bordered true
         :columns bid-columns
         :dataSource data
         :size "small"
         :rowKey :keyword-id
         :pagination {:showTotal (fn [total [start end]]
                                   (gstring/format "총 %s개 중 %s-%s" total start end))}}]]]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:optimize-detail
                        :optimize-settings
                        :current-client
                        :with-minimum-exposure-bids
                        :with-min-70-bids]}))
