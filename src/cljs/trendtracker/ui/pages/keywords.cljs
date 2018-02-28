(ns trendtracker.ui.pages.keywords
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [goog.string :as gstring]
            [keechma.toolbox.ui :refer [sub>]]
            [trendtracker.utils :as u]))

(def ^:private columns
  [{:title "광고그룹 ID" :dataIndex :adgroup-id}
   {:title "ID" :dataIndex :id}
   {:title "키워드" :dataIndex :keyword}
   {:title "Bid" :dataIndex :bid :render u/krw}])

(defn render [ctx]
  [:div
   [:div.content-header
    [(ui/component ctx :breadcrumbs)]
    [:h2 "키워드 목록"]]
   [:div.content
    [ant/card
     [ant/table
      {:columns columns
       :dataSource (sub> ctx :registered-keywords)
       :rowKey :id
       :size "middle"
       :bordered true
       :pagination {:hideOnSinglePage true
                    :showTotal (fn [total [start end]]
                                 (gstring/format "총 %s개 중 %s-%s" total start end))
                    :defaultPageSize 15
                    :pageSizeOptions ["15" "30" "45"]
                    :showSizeChanger true}}]]]])

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs]
    :subscription-deps [:registered-keywords]}))
