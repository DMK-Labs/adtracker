(ns trendtracker.ui.components.optimize.portfolio
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]
            [reagent.core :as r]
            [trendtracker.utils :as u]))

(defn campaign-list []
  (let [campaigns
        ;; TODO: move data to subscription
        [{:key "cmp-a001-01-000000000085482"
          :name "대쉬크랩_M"
          :id "cmp-a001-01-000000000085482"
          :status :on
          :kind "Powerlink"
          :clicks (repeatedly 30 #(hash-map :value (rand-int 900)))
          :cpc (repeatedly 30 #(hash-map :value (rand-int 900)))}
         {:key 2
          :name "대쉬크랩_스토어팜"
          :id "cmp-a001-01-000000000283508"
          :status :on
          :kind "Powerlink"
          :clicks (repeatedly 30 #(hash-map :value (rand-int 900)))
          :cpc (repeatedly 30 #(hash-map :value (rand-int 900)))}
         {:key 3
          :name "대쉬크랩_스토어팜_M"
          :id "cmp-a001-01-000000000285504"
          :status :processing
          :kind "Powerlink"
          :clicks (repeatedly 30 #(hash-map :value (rand-int 900)))
          :cpc (repeatedly 30 #(hash-map :value (rand-int 900)))}
         {:key 4
          :name "대쉬크랩_PC"
          :id "cmp-m001-01-000000042688965"
          :status :manual
          :kind "Powerlink"
          :clicks (repeatedly 30 #(hash-map :value (rand-int 900)))
          :cpc (repeatedly 30 #(hash-map :value (rand-int 900)))}]]
    [ant/card
     [ant/table
      {:dataSource campaigns
       :pagination (> (count campaigns) 8)
       ;; :rowSelection {}
       :columns
       [{:title "진행 여부" :dataIndex :status
         :render (fn [status]
                   (r/as-element
                     (case status
                       "on" [ant/badge {:status "success" :text "On"}]
                       "processing" [ant/badge {:status "processing" :text "Processing"}]
                       "manual" [ant/badge {:status "default" :text "Off"}])))}
        {:title "Type" :dataIndex :kind}
        {:title "Name" :dataIndex :name
         :render (fn [text record idx]
                   (r/as-element
                     [:a
                      {:href (str
                              "https://manage.searchad.naver.com/customers/777309/campaigns/"
                              (:id record))
                       :target "_blank"}
                      text]))}
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
                                      :dot nil}]]])}]}]]))

(def component
  (ui/constructor
   {:renderer campaign-list}))
