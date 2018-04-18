(ns trendtracker.ui.components.keyword-tool.results-table
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [route> sub>]]
            [keechma.ui-component :as ui]
            [reagent.core :as r]
            [trendtracker.helpers.download :as download]
            [trendtracker.utils :as u]
            [trendtracker.options :as opts]))

(def columns
  [{:title "키워드" :dataIndex :keyword :fixed :left :width 220}
   {:title "기기" :dataIndex :device :fixed :left :width 80
    :filters [{:text "PC" :value "PC"}
              {:text "Mobile" :value "MOBILE"}]
    :onFilter (fn [value record]
                (-> record
                    (js->clj :keywordize-keys true)
                    :device
                    (= value)))}
   {:title "1순위 평균"
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
   {:title "2순위 평균"
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
   {:title "3순위 평균"
    :children [{:title "입찰"
                :dataIndex :3-bid
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :3-bid)}
               {:title "노출"
                :dataIndex :3-impressions
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :3-impressions)}
               {:title "클릭"
                :dataIndex :3-clicks
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :3-clicks)}
               {:title "비용"
                :dataIndex :3-cost
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :3-cost)}]}
   {:title "4순위 평균"
    :children [{:title "입찰"
                :dataIndex :4-bid
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :4-bid)}
               {:title "노출"
                :dataIndex :4-impressions
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :4-impressions)}
               {:title "클릭"
                :dataIndex :4-clicks
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :4-clicks)}
               {:title "비용"
                :dataIndex :4-cost
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :4-cost)}]}
   {:title "5순위 평균"
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
                :sorter (u/sorter-by :5-cost)}]}
   {:title "Median 입찰가"
    :children [{:title "입찰"
                :dataIndex :median-bid
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :median-bid)}
               {:title "노출"
                :dataIndex :median-impressions
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :median-impressions)}
               {:title "클릭"
                :dataIndex :median-clicks
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :median-clicks)}
               {:title "비용"
                :dataIndex :median-cost
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :median-cost)}]}
   {:title "최소노출 입찰가"
    :children [{:title "입찰"
                :dataIndex :min-bid
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :min-bid)}
               {:title "노출"
                :dataIndex :min-impressions
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :min-impressions)}
               {:title "클릭"
                :dataIndex :min-clicks
                :render #(r/as-element (u/int-fmt %))
                :sorter (u/sorter-by :min-clicks)}
               {:title "비용"
                :dataIndex :min-cost
                :render #(r/as-element (u/krw %))
                :sorter (u/sorter-by :min-cost)}]}])

(defn render [ctx]
  (let [data (:result (sub> ctx :keyword-tool))
        value (r/atom 1)]
    (fn []
      [:div
       [ant/alert
        {:message "모든 지표는 네이버 전체 시스템 내의 과거 28일(4주)의 성과를 비롯해 산출되었습니다."
         :banner true
         :closable true
         :style {:margin-bottom 16}}]
       [ant/row {:type :flex :justify :space-between}
        [:div [ant/select {:defaultValue @value
                           :onChange #(reset! value %)}
               [ant/select-option {:value 1} "1위"]
               [ant/select-option {:value 2} "2위"]
               [ant/select-option {:value 3} "3위"]
               [ant/select-option {:value 4} "4위"]
               [ant/select-option {:value 5} "5위"]]
         [:ul
          [:li "비용: " (u/krw (u/sum (keyword (str @value "-cost")) data))]
          [:li "노출수: " (u/int-fmt (u/sum (keyword (str @value "-impressions")) data))]
          [:li "클릭수: " (u/int-fmt (u/sum (keyword (str @value "-clicks")) data))]]]

        [ant/button {:on-click #(download/download-csv
                                 {:filename "keyword_discovery.csv"
                                  :header [:keyword :device :keywordplus
                                           :1-bid :1-impressions :1-clicks :1-cost
                                           :2-bid :2-impressions :2-clicks :2-cost
                                           :3-bid :3-impressions :3-clicks :3-cost
                                           :4-bid :4-impressions :4-clicks :4-cost
                                           :5-bid :5-impressions :5-clicks :5-cost
                                           :median-bid :median-impressions :median-clicks :median-cost
                                           :min-bid :min-impressions :min-clicks :min-cost]
                                  :content data
                                  :prepend-header true})
                     :icon "download"}
         "CSV로 다운로드"]]
       (if data
         [ant/table
          {:scroll {:x 2700}
           :dataSource (map #(assoc % :key (str (:keyword %) (:device %)))
                            data)
           :columns columns
           :size "small"
           :bordered true
           :pagination opts/pagination
           :rowSelection {:fixed true}}]
         (ui/redirect ctx (dissoc (route> ctx) :result)))])))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:keyword-tool]}))
