(ns trendtracker.ui.components.tables.segment-stats
  (:require [keechma.ui-component :as ui]
            [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub> route> <cmd]]
            [trendtracker.utils :as u]
            [reagent.core :as r]
            [goog.string :as gstring]
            [trendtracker.helpers.download :as download]))

(defn title-renderer
  "tp is campaigns or adgroups"
  [customer-id tp]
  (fn [text record _]
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
  [ctx customer-id]
  (let [stats (sub> ctx :segment-stats)
        default [{:title "구분" :dataIndex :name
                  :render (title-renderer customer-id "adgroups")
                  :fixed "left"}
                 {:title "평균노출순위" :dataIndex :avgRnk
                  :sorter (u/sorter-by :avgRnk)
                  :className "numbers"}
                 {:title "비용" :dataIndex :salesAmt :render #(u/krw %)
                  :sorter (u/sorter-by :salesAmt)
                  :className "numbers"}
                 {:title "노출수" :dataIndex :impCnt :render #(u/int-fmt %)
                  :sorter (u/sorter-by :impCnt)
                  :className "numbers"}
                 {:title "클릭수" :dataIndex :clkCnt :render #(u/int-fmt %)
                  :sorter (u/sorter-by :clkCnt)
                  :className "numbers"}
                 {:title "클릭률" :dataIndex :ctr :render #(str % "%")
                  :sorter (u/sorter-by :ctr)
                  :className "numbers"}
                 {:title "CPC" :dataIndex :cpc :render #(u/krw %)
                  :sorter (u/sorter-by :cpc)
                  :className "numbers"}
                 {:title "전환수" :dataIndex :ccnt :render #(u/int-fmt %)
                  :sorter (u/sorter-by :ccnt)
                  :className "numbers"}
                 {:title "전환률" :dataIndex :crto :render #(str % "%")
                  :sorter (u/sorter-by :crto)
                  :className "numbers"}
                 {:title "전환매출" :dataIndex :convAmt :render #(u/krw %)
                  :sorter (u/sorter-by :convAmt)
                  :className "numbers"}
                 {:title "이윤" :dataIndex :profit :render #(u/krw %)
                  :sorter (u/sorter-by :profit)
                  :className "numbers"}
                 {:title "ROAS" :dataIndex :roas :render #(u/pct-fmt %)
                  :sorter (u/sorter-by :roas)
                  :className "numbers"}]
        kw-columns (cons {:title "구분" :dataIndex :keyword :fixed true}
                         (map #(assoc %
                                      :className "numbers"
                                      :sorter (u/sorter-by (:dataIndex %)))
                              [{:title "노출순위" :dataIndex :avg-rank
                                :render #(u/dec-fmt 2 %)}
                               {:title "비용" :dataIndex :cost
                                :render (comp u/krw int)}
                               {:title "노출수" :dataIndex :impressions
                                :render u/int-fmt}
                               {:title "CPM" :dataIndex :cpm
                                :render (comp u/krw int)}
                               {:title "클릭수" :dataIndex :clicks
                                :render u/int-fmt}
                               {:title "클릭률" :dataIndex :ctr
                                :render #(u/pct-fmt %)}
                               {:title "CPC" :dataIndex :cpc
                                :render (comp u/krw int)}
                               {:title "전환수" :dataIndex :conversions
                                :render u/int-fmt}
                               {:title "전환률" :dataIndex :cvr
                                :render #(u/pct-fmt %)}
                               {:title "CPA" :dataIndex :cpa
                                :render (comp u/krw int)}
                               {:title "전환매출" :dataIndex :revenue
                                :render u/krw}
                               {:title "이윤" :dataIndex :profit
                                :render u/krw}]))]
    (if (= "keyword" (:seg (route> ctx)))
      kw-columns
      default)))

(defn render [ctx]
  (let [stats (sub> ctx :segment-stats)
        route (route> ctx)
        customer-id (:customer_id (sub> ctx :current-client))
        columns (columns ctx customer-id)
        table-opts {:columns columns
                    :dataSource stats
                    :loading (nil? stats)
                    :size :middle
                    :pagination {:hideOnSinglePage true
                                 :showTotal (fn [total [start end]]
                                              (gstring/format "총 %s개 중 %s-%s" total start end))
                                 :defaultPageSize 10
                                 :pageSizeOptions ["10" "20" "30"]
                                 :showSizeChanger true}}]
    [ant/card
     {:title (r/as-element
              [ant/select {:defaultValue (or (:seg route) "campaign")
                           :onSelect #(ui/redirect ctx (assoc route :seg %))
                           :style {:font-size 16}}
               [ant/select-option {:value "campaign"} "캠페인 성과 지표"]
               [ant/select-option {:value "adgroup"} "광고그룹 성과 지표"]
               [ant/select-option {:value "keyword"} "키워드 성과 지표"]])
      :extra (r/as-element
              [ant/button {:onClick #(download/download-csv
                                      {:filename "test.csv"
                                       :header (map :dataIndex columns)
                                       :content stats
                                       :prepend-header true})
                           :icon "download"}
               "CSV 다운로드"])}
     [ant/table
      (if (= "keyword" (:seg route))
        (merge table-opts
               {:rowKey :keyword
                :size :middle
                :scroll {:x "1200px"}})
        (merge table-opts
               {:rowKey :id
                :scroll {:x "1200px"}}))]]))

(def component
  (ui/constructor
   {:renderer render
    :subject :segment-stats
    :subscription-deps [:segment-stats
                        :current-client]}))

