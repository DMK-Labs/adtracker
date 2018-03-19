(ns trendtracker.ui.pages.keywords
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [goog.string :as gstring]
            [keechma.toolbox.ui :refer [sub>]]
            [trendtracker.utils :as u]
            [trendtracker.options :as opts]
            [reagent.core :as r]))

(def ^:private columns
  [{:title "광고그룹 ID" :dataIndex :adgroup-id}
   ;; {:title "ID" :dataIndex :id}
   {:title "키워드" :dataIndex :keyword
    :renderer (fn [text record _]
                (r/as-element 
                 (str text (:id record))))}
   {:title "Bid" :dataIndex :bid :render u/krw}])

(defn render [ctx]
  (let [data (sub> ctx :registered-keywords)]
    [:div
     [:div.content-header
      [(ui/component ctx :breadcrumbs)]
      [:h2 "키워드 목록"]]
     [:div.content
      ;; [ant/card
      ;;  [:code (pr-str (take 5 data))]]
      [ant/card
       [ant/table
        {:columns columns
         :dataSource data
         :rowKey :id
         :size "middle"
         :pagination opts/pagination}]]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs]
    :subscription-deps [:registered-keywords]}))
