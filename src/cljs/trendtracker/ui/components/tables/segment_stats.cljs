(ns trendtracker.ui.components.tables.segment-stats
  (:require [antizer.reagent :as ant]
            [clojure.set :as set]
            [keechma.toolbox.ui :refer [route> sub>]]
            [keechma.ui-component :as ui]
            [reagent.core :as r]
            [trendtracker.helpers.download :as download]
            [trendtracker.options :as opts]
            [trendtracker.utils :as u]))

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

(def kw-columns (cons {:title "구분" :dataIndex :keyword :fixed true}
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
                             :render u/krw}])))

(defn columns
  [ctx customer-id]
  (let [stats (sub> ctx :segment-stats)
        default [{:title "구분" :dataIndex :name
                  :render (title-renderer customer-id "adgroups")
                  ;; :fixed "left"
                  }
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
                  :className "numbers"}]]
    (if (= "keyword" (:seg (route> ctx)))
      kw-columns
      default)))

;; (defn expanded-row-render [ctx]
;;   (fn [row]
;;     (let [cols kw-columns
;;          data []]
;;      (r/as-element
;;       ;; [ant/table {:columns cols}]
;;       (str (:id (js->clj row :keywordize-keys true)))))))

;; (defn on-expanded-rows-change 
;;   "Takes the `ctx` and builds a routing fn which encodes `rows` into the URL."
;;   [ctx]
;;   (fn [rows]
;;     (let [route (route> ctx)
;;           old-rows (set (:exp route))
;;           diff (set/difference (set rows) old-rows)] 
;;       (ui/redirect ctx (assoc route :exp diff)))))

(defn render [ctx]
  (let [stats       (sub> ctx :segment-stats)
        route       (route> ctx)
        customer-id (:customer_id (sub> ctx :current-client))
        columns     (columns ctx customer-id)
        table-opts  {:columns    columns
                     :dataSource stats
                     :loading    (nil? stats)
                     :size       :middle
                     :pagination opts/pagination}]
    [ant/card
     {:title (r/as-element
              [ant/select {:defaultValue (or (:seg route) "adgroup")
                           :onSelect     #(ui/redirect ctx (assoc route :seg %))
                           :style        {:font-size 16}}
               ;; [ant/select-option {:value "campaign"} "캠페인 성과 지표"]
               [ant/select-option {:value "adgroup"} "광고그룹 성과 지표"]
               [ant/select-option {:value "keyword"} "키워드 성과 지표"]])
      :extra (r/as-element
              [ant/button {:onClick #(download/download-csv
                                      {:filename       "test.csv"
                                       :header         (map :dataIndex columns)
                                       :content        stats
                                       :prepend-header true})
                           :icon    "download"}
               "CSV 다운로드"])}
     [ant/table
      (if (= "keyword" (:seg route))
        (merge table-opts
               {:rowKey :keyword
                :size   :middle
                :scroll {:x "1200px"}})
        (merge table-opts
               {:rowKey :id
                ;; :expandedRowRender (expanded-row-render ctx)
                ;; :onExpandedRowsChange (on-expanded-rows-change ctx) 
                ;; :expandRowByClick true
                :scroll {:x "1200px"}}))]]))

(def component
  (ui/constructor
   {:renderer render
    :subject :segment-stats
    :subscription-deps [:segment-stats
                        :current-client]}))
