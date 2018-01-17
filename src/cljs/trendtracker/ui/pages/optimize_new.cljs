(ns trendtracker.ui.pages.optimize-new
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [route>]]))

(defn request-optimization [ctx evt]
  (do
    (ui/redirect ctx {:page "optimize"})
    (js/setTimeout
     (fn []
       (ant/notification-success
        {:message "최적화 실시"
         :description "AdTracker는 고객님의 입찰 광고 캠페인의 효율을 극대화 하기 위한 최적화를 실시하고 있습니다."}))
     500)))

(defn render [ctx]
  (let [route (route> ctx)
        step (dec (js/parseInt (:step route)))
        steps [{:title "대상 및 목표 선택"
                :content [:div "step 1 stuff"]}
               {:title "예산 설정"
                :content [:div "step 2 stuff"]}
               {:title "세부 지표 확인"
                :content [:div "step 3 stuff"]}]
        btn-style {:margin-right 8 :margin-top 16}]
    [:div
     [:div.content-header
      [ant/row [:h1 "네이버 검색광고 입찰 최적화"]]
      [ant/row [:p "최적화를 진행합니다. 원하시는 월 예산 한도와 최대화 하고 싶은 지표를 고시오."]]
      [ant/steps {:current step}
       (map (fn [{title :title}]
              [ant/steps-step {:key title :title title}])
            steps)]]
     [:div.content
      [ant/card
       [:div
        (:content (get steps step))
        [ant/button
         {:on-click #(ui/redirect ctx (update route :step (comp dec js/parseInt)))
          :style btn-style
          :disabled (= 0 step)}
         "이전"]
        (when (< step 2)
          [ant/button
           {:on-click #(ui/redirect ctx (update route :step (comp inc js/parseInt)))
            :type "primary" :style btn-style}
           "다음"])
        (when (= step 2)
          [ant/button
           {:on-click #(request-optimization ctx %)
            :type "primary" :style btn-style}
           "자동 최적화 실시"])]]]]))

(def component
  (ui/constructor
    {:renderer render}))

