(ns trendtracker.ui.pages.insights.best-ads
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> route>]]
            [reagent.core :as r]
            [trendtracker.ui.components.common :as common]
            [trendtracker.options :as opts]
            [trendtracker.utils :as u]))

(defn ad-render [customer-id]
  (fn [_ record _]
    (let [record-map (js->clj record :keywordize-keys true)]
      (r/as-element
       [:div
        [:a {:href (str "https://manage.searchad.naver.com/customers/"
                        customer-id "/searchs?q="
                        (:ad-id record-map)
                        "&exact=false")
             :target "_blank"}
         (:subject record-map)]
        [:div (:description record-map)]]))))

(defn adgroup-render [customer-id]
  (fn [text record _]
    (let [record-map (js->clj record :keywordize-keys true)]
      (r/as-element
       [:a {:href (str "https://manage.searchad.naver.com/customers/"
                       customer-id "/adgroups/"
                       (:adgroup-id record-map))
            :target "_blank"}
        text]))))


(defn columns [customer-id]
  [{:title "Adgroup" :dataIndex :adgroup :render (adgroup-render customer-id)}
   {:title "Ad ID" :dataIndex :ad-id :render (ad-render customer-id)}
   {:title "Cost" :dataIndex :cost :sorter (u/sorter-by :cost)
    :render #(r/as-element (u/krw %))}
   {:title "Clicks" :dataIndex :clicks :sorter (u/sorter-by :clicks)}
   {:title "CTR" :dataIndex :ctr :sorter (u/sorter-by :ctr)
    :render #(r/as-element (u/pct-fmt %))}])

(defn render [ctx]
  (let [breadcrumbs (ui/component ctx :breadcrumbs)
        best-ads (sub> ctx :best-ads)
        customer-id (:customer_id (sub> ctx :current-client))]
    [:div
     [common/content-header
      [breadcrumbs]
      [:h2.page-title "Ad Creative Greatest Hits"]]
     [ant/spin {:spinning (empty? best-ads)}
      [:div.content
       [ant/card
        [ant/table
         {:className "adcreative"
          :bordered true
          :dataSource best-ads
          :size "small"
          :columns (columns customer-id)
          :rowKey :adgroup-id
          :pagination (assoc opts/pagination :defaultPageSize 20)}]]]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs]
    :subscription-deps [:best-ads
                        :current-client]}))
