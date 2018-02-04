(ns trendtracker.ui.components.keyword-tool.results-table
  (:require [antizer.reagent :as ant]
            [goog.string :as gstring]
            [keechma.toolbox.ui :refer [sub> route>]]
            [keechma.ui-component :as ui]
            [reagent.core :as r]
            [trendtracker.utils :as u]
            [goog.string.format]))

(def columns
  [{:title "키워드" :dataIndex :keyword :width 150 :fixed :left}
   {:title "기기" :width 80 :fixed :left :dataIndex :device
    :filters [{:text "PC" :value "PC"}
              {:text "Mobile" :value "MOBILE"}]
    :onFilter (fn [value record]
                (-> record
                    (js->clj :keywordize-keys true)
                    :device
                    (= value)))}
   {:title "30일간 1순위 평균"
    :children [{:title "입찰"
                :dataIndex :1-bid
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :1-bid)}
               {:title "노출"
                :dataIndex :1-impressions
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :1-impressions)}
               {:title "클릭"
                :dataIndex :1-clicks
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :1-clicks)}
               {:title "비용"
                :dataIndex :1-cost
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :1-cost)}]}
   {:title "30일간 2순위 평균"
    :children [{:title "입찰"
                :dataIndex :2-bid
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :2-bid)}
               {:title "노출"
                :dataIndex :2-impressions
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :2-impressions)}
               {:title "클릭"
                :dataIndex :2-clicks
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :2-clicks)}
               {:title "비용"
                :dataIndex :2-cost
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :2-cost)}]}
   {:title "30일간 5순위 평균"
    :children [{:title "입찰"
                :dataIndex :5-bid
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :5-bid)}
               {:title "노출"
                :dataIndex :5-impressions
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :5-impressions)}
               {:title "클릭"
                :dataIndex :5-clicks
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :5-clicks)}
               {:title "비용"
                :dataIndex :5-cost
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :5-cost)}]}])

(defn render [ctx]
  (let [data (sub> ctx :keyword-tool)]
    [ant/row
     [ant/card
      [ant/row {:style {:margin-bottom 16} :type "flex" :justify "space-between"}
       [ant/button
        {:on-click #(ui/redirect ctx {:page "keyword-tool"
                                      :client (:client (route> ctx))})
         :icon "left"}
        "키워드 변경"]
       [ant/button {:on-click #(u/download-csv "keyword_discovery.csv"
                                               [:keyword :device :keywordplus
                                                :1-bid :1-impressions :1-clicks :1-cost
                                                :2-bid :2-impressions :2-clicks :2-cost
                                                :5-bid :5-impressions :5-clicks :5-cost]
                                               (:result data))
                    :icon "download"}
        "XLSX로 다운로드"]]
      (if data
        [ant/table
         {:scroll {:x 1500}
          :dataSource (map #(assoc % :key (str (:keyword %) (:device %))) (:result data))
          :columns columns
          :size "middle"
          :bordered true
          :pagination {:hideOnSinglePage true
                       :showTotal (fn [total [start end]]
                                    (gstring/format "총 %s개 중 %s-%s" total start end))
                       :defaultPageSize 15
                       :pageSizeOptions ["15" "30" "45"]
                       :showSizeChanger true}}]
        (ui/redirect ctx (assoc (route> ctx)
                                :page "keyword-tool")))]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:keyword-tool]}))
