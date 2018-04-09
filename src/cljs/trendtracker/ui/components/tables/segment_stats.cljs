(ns trendtracker.ui.components.tables.segment-stats
  (:require [antizer.reagent :as ant]
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

(defn adgroup-title-renderer
  [ctx]
  (fn [text record _]
    (r/as-element
     [:a {:href (ui/url ctx (assoc (route> ctx)
                              :adgrp (-> record
                                         (js->clj :keywordize-keys true)
                                         :adgroup-id)
                              :seg "keyword"))}
      text])))

(def kw-columns
  (cons {:title "구분" :dataIndex :keyword_id :fixed true}
        (map #(assoc %
                :className "numbers"
                :sorter (u/sorter-by (:dataIndex %)))
             [{:title "노출순위"
               :dataIndex :avg-rank
               :render #(if (nil? %)
                          "-"
                          (u/dec-fmt 2 %))}
              {:title "비용" :dataIndex :cost :render (comp u/krw int)}
              {:title "노출수" :dataIndex :impressions :render u/int-fmt}
              {:title "CPM" :dataIndex :cpm :render (comp u/krw int)}
              {:title "클릭수" :dataIndex :clicks :render u/int-fmt}
              {:title "클릭률" :dataIndex :ctr :render #(u/pct-fmt %)}
              {:title "CPC" :dataIndex :cpc :render (comp u/krw int)}
              {:title "전환수" :dataIndex :conversions :render u/int-fmt}
              {:title "전환률" :dataIndex :cvr :render #(u/pct-fmt %)}
              {:title "CPA" :dataIndex :cpa :render (comp u/krw int)}
              {:title "전환매출" :dataIndex :revenue :render u/krw}
              {:title "이윤" :dataIndex :profit :render u/krw}])))

(defn columns
  [ctx customer-id]
  (let [stats (sub> ctx :segment-stats)]
    (if (= "keyword" (:seg (route> ctx)))
      kw-columns
      (cons {:title "구분"
             :dataIndex :name
             :fixed true
             :render (adgroup-title-renderer ctx)}
            (rest kw-columns)))))

(defn render [ctx]
  (let [stats (sub> ctx :segment-stats)
        route (route> ctx)
        customer-id (:customer_id (sub> ctx :current-client))
        columns (columns ctx customer-id)
        table-opts {:columns columns
                    :dataSource stats
                    :loading (nil? stats)
                    :size :middle
                    :pagination opts/pagination}]
    [ant/card
     {:title "광고그룹 성과 지표"
      #_(r/as-element
         [:div
          [ant/select {:value (or (:seg route) "adgroup")
                       :onSelect #(ui/redirect
                                   ctx
                                   (-> route
                                       (assoc :seg %)
                                       (dissoc :adgrp)))
                       :style {:font-size 16}}
           ;; [ant/select-option {:value "campaign"} "캠페인 성과 지표"]
           [ant/select-option {:value "adgroup"} "광고그룹 성과 지표"]
           [ant/select-option {:value "keyword"} "키워드 성과 지표"]]])
      :extra (r/as-element
              [:a
               {:onClick #(download/download-csv
                           {:filename "test.csv"
                            :header (map :dataIndex columns)
                            :content stats
                            :prepend-header true})}
               "CSV "
               [ant/icon {:type "download"}]])}
     [ant/table
      (if (= "keyword" (:seg route))
        (merge
         table-opts
         {:rowKey :keyword_id
          :scroll {:x "1200px"}
          :size :middle})
        (merge
         table-opts
         {:scroll {:x "1200px"}
          :rowKey :id}))]]))

(def component
  (ui/constructor
   {:renderer render
    :subject :segment-stats
    :subscription-deps [:segment-stats
                        :current-client]}))
