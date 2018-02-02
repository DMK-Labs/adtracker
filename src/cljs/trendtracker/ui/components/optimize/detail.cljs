(ns trendtracker.ui.components.optimize.detail
  (:require [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [route>]]
            [antizer.reagent :as ant]))

(def columns
  [{:title "광고 구분" :key "Type"}
   {:title "캠페인" :key "Campaign"}
   {:title "캠페인 ID" :key "Campaign ID"}
   {:title "광고그룹" :key "Adgroup"}
   {:title "광고그룹 ID" :key "Adgroup ID"}
   {:title "최적화 전" :key "previous"
    :children
    [{:title "노출" :key "노출"}
     {:title "클릭" :key "클릭"}
     {:title "비용" :key "비용"}
     {:title "순위(평균)" :key "순위(평균)"}
     {:title "전환" :key "전환"}
     {:title "매출" :key "매출"}]}
   {:title "최적화 후 (예측)"
    :key "optimized"
    :children
    [{:title "노출" :key "opt노출"}
     {:title "클릭" :key "opt클릭"}
     {:title "비용" :key "opt비용"}
     {:title "순위(평균)" :key "opt순위(평균)"}
     {:title "전환" :key "opt전환"}
     {:title "매출" :key "opt매출"}]}])

(defn request-optimization [ctx _]
  (do
    (ui/redirect ctx {:page "optimize"
                      :client (:client (route> ctx))})
    (js/setTimeout
     (fn []
       (ant/notification-success
        {:message "최적화 실시"
         :description "AdTracker는 고객님의 입찰 광고 캠페인의 효율을 극대화 하기 위한 최적화를 실시하고 있습니다."}))
     300)))

(defn render [ctx]
  [ant/card
   [ant/row {:style {:margin-bottom 16}}
    [ant/table
     {:scroll {:x 1500}
      :bordered true
      :size "middle"
      :columns columns}]]
   [ant/form-item {:style {:margin-bottom 0}}
    [ant/button-group
     [ant/button
      {:on-click #(ui/redirect ctx (assoc (route> ctx)
                                          :page "optimize"
                                          :subpage "new"
                                          :step 2))
       :icon "left"}
      "이전"]
     [ant/button
      {:on-click #(request-optimization ctx %)
       :type "primary"
       :icon "download"}
      "대량관리 CSV 내려 받기"]
     [ant/button
      {:on-click #(request-optimization ctx %)
       :type "primary"
       :icon "rocket"
       :disabled true}
      "자동 최적화 실시"]]]])

(def component
  (ui/constructor
    {:renderer render}))
