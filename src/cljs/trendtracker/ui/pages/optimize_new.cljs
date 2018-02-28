(ns trendtracker.ui.pages.optimize-new
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [route>]]))

(defn render [ctx]
  (let [breadcrumbs (ui/component ctx :breadcrumbs)
        budgeting (ui/component ctx :budgeting)]
    [:div
     [:div.content-header
      [breadcrumbs]
      [:h2 "네이버 검색광고 입찰 최적화"]
      [:p "Slider를 움직이면 AdTracker가 예측해 드리는 사후 30일간 성과 지표들을 확인하실 수 있습니다."]
      [ant/alert {:message "초기의 예측 정보는 네이버 플랫폼상 등록된 해당 키워드 및 순위에 노출되는 광고의 평균
                            지표를 기반으로 산출합니다. 많이 사용하실 수록 AdTracker가 고객님의 광고의
                            실적을 학습하며 더욱 맞춤형 예측을 드릴 수 있습니다."
                  :banner true
                  :closable true}]]
     [:div.content
      [budgeting]]]))

(def component
  (ui/constructor
    {:renderer render
     :component-deps [:budgeting
                      :objective
                      :detail
                      :breadcrumbs]}))

