(ns trendtracker.ui.components.keyword-tool.results-table
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [trendtracker.utils :as u]
            [reagent.core :as r]))

(def columns
  [{:title "키워드"
    :dataIndex :keyword}
   {:title "기기"
    :dataIndex :device
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
        {:on-click #(ui/redirect ctx {:page "keyword-tool"})
         :icon "left"}
        "키워드 변경"]
       [ant/button
        {:on-click #()
         :icon "download"}
        "XLSX로 다운로드"]]
      (if data
        [ant/table
         {:dataSource (:result data)
          :columns columns
          :size "middle"
          :bordered true
          :rowKey :dataIndex}]
        [ant/alert {:message "검색된 키워드 결과가 없습니다. 키워드 목록을 확인하시고 다시 검색해 주십시오."
                    :type "warning"
                    :showIcon true}])]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:keyword-tool]}))
