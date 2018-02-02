(ns trendtracker.ui.pages.optimize-new
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [route>]]
            [reagent.core :as r]
            [trendtracker.utils :as u]
            [reacharts.recharts :as recharts]))

(defn render [ctx]
  (let [route (route> ctx)
        step (dec (js/parseInt (:step route)))

        breadcrumbs (ui/component ctx :breadcrumbs)
        objective (ui/component ctx :objective)
        budgeting (ui/component ctx :budgeting)
        detail (ui/component ctx :detail)
        steps [{:title "대상 및 목표 선택" :content [objective]}
               {:title "예산 설정" :content [budgeting]}
               {:title "세부 지표 확인" :content [detail]}]]
    [:div
     [:div.content-header
      [breadcrumbs]
      [ant/row [:h2 "네이버 검색광고 입찰 최적화"]]
      [ant/row
       (case step
         0 [:p "최적화를 진행할 목표 지표와, 대상 캠페인을 선택하십시오."]
         1 [:p "Slider를 움직이면 예측되는 30일간 예산과 해당 (예측) 성과 지표들을 확인할 수 있습니다."]
         2 [:p "각 키워드, 광고별 변경 사항을 확인하신 후 \"자동 최적화 실시\" 버튼을 누르십시오. 앞으로는 설정하신 캠페인, 목표 지표 대상으로 상시 최적화를 진행합니다."])]
      [ant/steps {:current step}
       (map (fn [{title :title}]
              [ant/steps-step {:key title :title title}])
            steps)]]
     [:div.content
      (:content (get steps step))]]))

(def component
  (ui/constructor
    {:renderer render
     :component-deps [:budgeting
                      :objective
                      :detail
                      :breadcrumbs]}))

