(ns trendtracker.ui.components.optimize.portfolio
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub>]]
            [reacharts.recharts :as recharts]
            [reagent.core :as r]
            [trendtracker.utils :as u]))

(defn render-campaign-title
  [text record idx]
  (r/as-element
   [:a {:href (str "https://manage.searchad.naver.com/customers/777309/campaigns/" (:id record))
        :target "_blank"}
    text]))

(def columns
  [{:title "광고 구분" :dataIndex :campaign-type}
   {:title "캠페인 명" :dataIndex :label
    :render render-campaign-title}
   {:title "진행 여부" :dataIndex :status
    :render (fn [status]
              (r/as-element
               (case status
                 "on" [ant/badge {:status "success" :text "On"}]
                 "processing" [ant/badge {:status "processing" :text "Processing"}]
                 "manual" [ant/badge {:status "default" :text "Off"}])))}
   {:title "예상 비용" :dataIndex :expected-cost
    :render (fn [cost]
              (u/krw cost))}
   ;; {:title "최근 30일 Clicks" :dataIndex :clicks
   ;;  :render #(r/as-element
   ;;            [:div
   ;;             (u/int-fmt (apply + (map (fn [m] (get m "value")) (js->clj %))))
   ;;             [recharts/line-chart {:height 24 :width 120 :data %}
   ;;              [recharts/line {:isAnimationActive false
   ;;                              :dataKey :value
   ;;                              :type :monotone
   ;;                              :dot nil}]]])}
   ;; {:title "최근 30일 CPC" :dataIndex :cpc
   ;;  :render #(r/as-element
   ;;            [:div
   ;;             "₩"
   ;;             (u/dec-fmt 1 (u/avg (map (fn [m] (get m "value")) (js->clj %))))
   ;;             [recharts/line-chart {:height 24 :width 120 :data %}
   ;;              [recharts/line {:isAnimationActive false
   ;;                              :dataKey :value
   ;;                              :type :monotone
   ;;                              :dot nil}]]])}
   ])

(defn campaign-list [ctx]
  (let [optimizing (sub> ctx :portfolio-optimizing)]
    [ant/table
     {:dataSource optimizing
      :loading (empty? optimizing)
      :pagination {:hideOnSinglePage true}
      :columns columns
      :expandedRowKeys (map :value optimizing)
      :rowKey :value}]))

(def component
  (ui/constructor
   {:renderer campaign-list
    :subscription-deps [:portfolio-optimizing]}))
