(ns trendtracker.ui.components.tables.adgroup-performances
  (:require [keechma.ui-component :as ui]
            [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub> route>]]
            [trendtracker.options :as opts]
            [trendtracker.utils :as u]
            [reagent.core :as r]
            [goog.string :as gstring]
            [goog.string.format]
            [clojure.string :as string]))

(def krw-render (comp u/krw int))

(defn rank-render [rank]
  (if (nil? rank) "-" (u/dec-fmt 2 rank)))

(defn title-renderer [customer-id url-fmt k]
  (fn [text record _]
    (r/as-element
     [:a {:href (str "https://manage.searchad.naver.com/customers/"
                     customer-id
                     (gstring/format url-fmt
                                     (-> record
                                         (js->clj :keywordize-keys true)
                                         k)))
          :target "_blank"}
      text])))

(defn filters [data k]
  (set (map (fn [datum]
              {:text (k datum)
               :value (k datum)})
            data)))

(defn on-filter [k]
  (fn [value record]
    (-> record
        (js->clj :keywordize-keys true)
        k
        (= value))))

(defn campaign-renderer [customer-id]
  (title-renderer customer-id "/campaigns/%s" :campaign-id))

(defn adgroup-renderer [customer-id]
  (title-renderer customer-id "/adgroups/%s" :adgroup-id))

(defn kw-columns [customer-id data]
  (concat [{:title "광고그룹" :dataIndex :adgroup
            :render (adgroup-renderer customer-id)
            :fixed "left"}
           {:title "캠페인" :dataIndex :campaign
            :render (campaign-renderer customer-id)
            :filters (filters data :campaign)
            :onFilter (on-filter :campaign)}]
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
  (let [adgroups (sub> ctx :adgroups)
        adgroups-meta (sub> ctx :adgroups-meta)
        customer-id (:customer_id (sub> ctx :current-client))
        route (route> ctx)
        regex (if-let [q (:aq route)]
                (re-pattern q)
                #"")]
    [ant/table
     {:dataSource (filter
                   #(or (re-find regex (apply str (vals %)))
                        (re-find regex (string/lower-case (apply str (vals %)))))
                   (if (:zero-clicks route)
                     adgroups
                     (remove-clickless adgroups)))
      :columns (kw-columns customer-id adgroups)
      :loading (= :pending (:status adgroups-meta))
      :rowKey :adgroup-id
      :size "small"
      :scroll {:x 1400}
      :rowSelection {:fixed true}
      :pagination (assoc opts/pagination :defaultPageSize 15)}]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:adgroups :adgroups-meta
                        :current-client]}))
