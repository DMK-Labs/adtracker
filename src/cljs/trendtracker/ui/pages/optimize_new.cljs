(ns trendtracker.ui.pages.optimize-new
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [route>]]
            [reagent.core :as r]
            [trendtracker.utils :as u]
            [reacharts.recharts :as recharts]))

(defn request-optimization [ctx _]
  (do
    (ui/redirect ctx {:page "optimize"})
    (ant/notification-success
      {:message "최적화 실시"
       :description "AdTracker는 고객님의 입찰 광고 캠페인의 효율을 극대화 하기 위한 최적화를 실시하고 있습니다."})))

(defn button-row [ctx route step]
  [ant/row
   [ant/button-group
    (if (zero? step)
      [ant/button
       {:on-click #(ui/redirect ctx {:page "optimize"})
        :type "danger"}
       "취소"]
      [ant/button
       {:on-click #(ui/redirect ctx (update route :step (comp dec js/parseInt)))
        :icon "left"}
       "이전"])
    (when (< step 2)
      [ant/button
       {:on-click #(ui/redirect ctx (update route :step (comp inc js/parseInt)))
        :type "primary"}
       "다음" [ant/icon {:type "right"}]])
    (when (= step 2)
      [ant/button
       {:on-click #(request-optimization ctx %)
        :type "primary"
        :icon "rocket"}
       "자동 최적화 실시"])]])

(defn render [ctx]
  (let [route (route> ctx)
        step (dec (js/parseInt (:step route)))
        objective (ui/component ctx :objective)
        budgeting (ui/component ctx :budgeting)
        detail (ui/component ctx :detail)
        steps [{:title "대상 및 목표 선택" :content [objective]}
               {:title "예산 설정" :content [budgeting]}
               {:title "세부 지표 확인" :content [detail]}]]
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
        [button-row ctx route step]]]]]))

(def component
  (ui/constructor
    {:renderer render
     :component-deps [:budgeting
                      :objective
                      :detail]}))

