(ns trendtracker.ui.components.optimize.portfolio
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]
            [reagent.core :as r]
            [trendtracker.utils :as u]))

(def campaigns
  ;; TODO: move into edb subscription
  [{:key "powerlink"
    :name "ALL"
    :status :on
    :kind "파워링크"
    :clicks (repeatedly 30 #(hash-map :value (rand-int 900)))
    :cpc (repeatedly 30 #(hash-map :value (rand-int 900)))
    :children [{:key 1
                :name "대쉬크랩_스토어팜"
                :id "cmp-a001-01-000000000283508"
                :status :on
                :kind "파워링크"
                :clicks (repeatedly 30 #(hash-map :value (rand-int 900)))
                :cpc (repeatedly 30 #(hash-map :value (rand-int 900)))}
               {:key 2
                :name "대쉬크랩_스토어팜_M"
                :id "cmp-a001-01-000000000285504"
                :status :processing
                :kind "파워링크"
                :clicks (repeatedly 30 #(hash-map :value (rand-int 900)))
                :cpc (repeatedly 30 #(hash-map :value (rand-int 900)))}]}])

(defn render-campaign-title
  [text record idx]
  (r/as-element
   [:a {:href (str "https://manage.searchad.naver.com/customers/777309/campaigns/" (:id record))
        :target "_blank"}
    text]))

(def columns
  [{:title "광고 구분" :dataIndex :kind}
   {:title "캠페인 명" :dataIndex :name
    :render render-campaign-title}
   {:title "진행 여부" :dataIndex :status
    :render (fn [status]
              (r/as-element
               (case status
                 "on" [ant/badge {:status "success" :text "On"}]
                 "processing" [ant/badge {:status "processing" :text "Processing"}]
                 "manual" [ant/badge {:status "default" :text "Off"}])))}
   {:title "최근 30일 Clicks" :dataIndex :clicks
    :render #(r/as-element
              [:div
               (u/int-fmt (apply + (map (fn [m] (get m "value")) (js->clj %))))
               [recharts/line-chart {:height 24 :width 120 :data %}
                [recharts/line {:isAnimationActive false
                                :dataKey :value
                                :type :monotone
                                :dot nil}]]])}
   {:title "최근 30일 CPC" :dataIndex :cpc
    :render #(r/as-element
              [:div
               "₩"
               (u/dec-fmt 1 (u/avg (map (fn [m] (get m "value")) (js->clj %))))
               [recharts/line-chart {:height 24 :width 120 :data %}
                [recharts/line {:isAnimationActive false
                                :dataKey :value
                                :type :monotone
                                :dot nil}]]])}])

(defn campaign-list []
  [ant/table
   {:dataSource campaigns
    :pagination (> (count campaigns) 8)
    :size "middle"
    :columns columns
    :defaultExpandAllRows true}])

(def component
  (ui/constructor
   {:renderer campaign-list}))
