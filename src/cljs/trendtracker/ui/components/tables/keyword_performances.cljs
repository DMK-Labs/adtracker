(ns trendtracker.ui.components.tables.keyword-performances
  (:require [keechma.ui-component :as ui]
            [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub> route>]]
            [trendtracker.options :as opts]
            [trendtracker.utils :as u]
            [reagent.core :as r]))

(def krw-render (comp u/krw int))

(defn rank-render [rank]
  (if (nil? rank) "-" (u/dec-fmt 2 rank)))

(defn keyword-renderer [customer-id]
  (fn [text record _]
    (r/as-element
     [:a {:href (str "https://manage.searchad.naver.com/customers/"
                     customer-id "/searchs?q="
                     (-> record
                         (js->clj :keywordize-keys true)
                         :keyword-id)
                     "&exact=false")
          :target "_blank"}
      text])))

(defn campaign-renderer [customer-id]
  (fn [text record _]
    (r/as-element
     [:a {:href (str "https://manage.searchad.naver.com/customers/"
                     customer-id "/campaigns/"
                     (-> record
                         (js->clj :keywordize-keys true)
                         :campaign-id))
          :target "_blank"}
      text])))

(defn adgroup-renderer [customer-id]
  (fn [text record _]
    (r/as-element
     [:a {:href (str "https://manage.searchad.naver.com/customers/"
                     customer-id "/adgroups/"
                     (-> record
                         (js->clj :keywordize-keys true)
                         :adgroup-id))
          :target "_blank"}
      text])))

(defn kw-columns [customer-id data]
  (concat [{:title "키워드" :dataIndex :keyword
            :render (keyword-renderer customer-id)
            :fixed "left"}
           {:title "캠페인" :dataIndex :campaign
            :render (campaign-renderer customer-id)
            :filters (set (map (fn [datum]
                                {:text (:campaign datum)
                                 :value (:campaign datum)})
                           data))
            :onFilter (fn [value record]
                        (-> record
                            (js->clj :keywordize-keys true)
                            :campaign
                            (= value)))}
           {:title "광고그룹" :dataIndex :adgroup
            :render (adgroup-renderer customer-id)
            :filters (set (map (fn [datum]
                                 {:text (:adgroup datum)
                                  :value (:adgroup datum)})
                               data))
            :onFilter (fn [value record]
                        (-> record
                            (js->clj :keywordize-keys true)
                            :adgroup
                            (= value)))}
           {:title "기기" :dataIndex :device
            :filters [{:text "PC" :value "P"}
                      {:text "Mobile" :value "M"}]
            :onFilter (fn [value record]
                        (-> record
                            (js->clj :keywordize-keys true)
                            :device
                            (= value)))}]
          (map #(assoc %
                  :className "numbers"
                  :sorter (u/sorter-by (:dataIndex %)))
               [{:title "노출순위" :dataIndex :avg-rank :render rank-render}
                {:title "노출수" :dataIndex :impressions :render u/int-fmt}
                {:title "CPM" :dataIndex :cpm :render krw-render}
                {:title "클릭수" :dataIndex :clicks :render u/int-fmt}
                {:title "클릭률" :dataIndex :ctr :render u/pct-fmt}
                {:title "CPC" :dataIndex :cpc :render krw-render}
                {:title "전환수" :dataIndex :conversions :render u/int-fmt}
                {:title "전환률" :dataIndex :cvr :render u/pct-fmt}
                {:title "CPA" :dataIndex :cpa :render krw-render}
                {:title "비용" :dataIndex :cost :render krw-render}
                {:title "전환매출" :dataIndex :revenue :render u/krw}
                {:title "이윤" :dataIndex :profit :render u/krw
                 :fixed "right"}])))

(defn remove-clickless [keywords]
  (remove #(zero? (:clicks %)) keywords))

(defn render [ctx]
  (let [keywords (sub> ctx :keywords)
        keywords-meta (sub> ctx :keywords-meta)
        customer-id (:customer_id (sub> ctx :current-client))
        route (route> ctx)]
    [ant/table
     {:dataSource (if (:zero-clicks route)
                    keywords
                    (remove-clickless keywords))
      :columns (kw-columns customer-id keywords)
      :loading (= :pending (:status keywords-meta))
      :rowKey :keyword_id
      :size "small"
      :scroll {:x "1600px"}
      :rowSelection {:fixed true}
      :pagination (assoc opts/pagination :defaultPageSize 15)}]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:keywords :keywords-meta
                        :current-client]}))
