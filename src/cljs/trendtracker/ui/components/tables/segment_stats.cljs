(ns trendtracker.ui.components.tables.segment-stats
  (:require [keechma.ui-component :as ui]
            [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub> route>]]
            [trendtracker.utils :as u]
            [reagent.core :as r]
            [goog.string :as gstring]))

(defn render-campaign-title
  [text record idx]
  (r/as-element
   [:a {:href (str "https://manage.searchad.naver.com/customers/777309/campaigns/" (:id record))
        :target "_blank"}
    text]))

(defn title-renderer
  "tp is campaigns or adgroups"
  [customer-id tp]
  (fn [text record idx]
    (r/as-element
     [:a {:href (str "https://manage.searchad.naver.com/customers/"
                     customer-id "/"
                     tp "/"
                     (-> record
                         (js->clj :keywordize-keys true)
                         :id))
          :target "_blank"}
      text])))

(defn columns
  [customer-id]
  [{:title "구분" :dataIndex :name
    :render (title-renderer customer-id "adgroups")
    :fixed "left"}
   {:title "평균노출순위" :dataIndex :avgRnk
    :sorter (u/sorter-by :avgRnk)}
   {:title "비용" :dataIndex :salesAmt :render #(u/krw %)
    :sorter (u/sorter-by :salesAmt)}
   {:title "노출수" :dataIndex :impCnt :render #(u/int-fmt %)
    :sorter (u/sorter-by :impCnt)}
   {:title "클릭수" :dataIndex :clkCnt :render #(u/int-fmt %)
    :sorter (u/sorter-by :clkCnt)}
   {:title "클릭률" :dataIndex :ctr :render #(str % "%")
    :sorter (u/sorter-by :ctr)}
   {:title "CPC" :dataIndex :cpc :render #(u/krw %)
    :sorter (u/sorter-by :cpc)}])

(def conv-columns
  [{:title "전환수" :dataIndex :ccnt :render #(u/int-fmt %)
    :sorter (u/sorter-by :ccnt)}
   {:title "전환률" :dataIndex :crto :render #(str % "%")
    :sorter (u/sorter-by :crto)}
   {:title "전환매출" :dataIndex :convAmt :render #(u/krw %)
    :sorter (u/sorter-by :convAmt)}])

(defn render [ctx]
  (let [stats (sub> ctx :segment-stats)
        any-conversions? (some pos? (map :ccnt stats))
        route (route> ctx)
        customer-id (:customer_id (sub> ctx :current-client))
        columns (columns customer-id)]
    [ant/card {:title (r/as-element
                       [ant/select
                        {:defaultValue (or (:seg route) "campaign")
                         :onSelect (fn [value]
                                     (ui/redirect
                                      ctx
                                      (assoc route :seg value)))
                         :style {:font-size 18}}
                        [ant/select-option {:value "campaign"}
                         "캠페인 성과 지표"]
                        [ant/select-option {:value "adgroup"}
                         "광고그룹 성과 지표"]])}
     [ant/table
      {:columns (if any-conversions?
                  (concat columns conv-columns)
                  columns)
       :dataSource stats
       :rowKey :id
       :loading (nil? stats)
       :size "small"
       :pagination {:hideOnSinglePage true
                    :showTotal (fn [total [start end]]
                                 (gstring/format "총 %s개 중 %s-%s" total start end))
                    :defaultPageSize 10
                    :pageSizeOptions ["10" "20" "30"]
                    :showSizeChanger true}
       :scroll {:x (if any-conversions?
                     "1200px"
                     "900px")}}]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:segment-stats
                        :current-client]}))

(defn pie-render [])

(def pie-component
  (ui/constructor
   {:renderer pie-render
    :subscription-deps [:current-client]}))
