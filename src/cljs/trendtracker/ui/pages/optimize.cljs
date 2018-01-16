(ns trendtracker.ui.pages.optimize
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [:div.content-header
   [(ui/component ctx :breadcrumbs)]
   [:h1 "입찰 최적화"]
   [:p "관리중인 키워드를 자동 실시간 관리할 수 있는 AI 기반 입찰 툴입니다."]
   [:p "일반적인 솔루션과 달리 단순히 순위를 유지하는 것만이 아니라, 주어진 예산
   안에서 원하는 성과를 극대화 할 수 있도록 상시 변하는 입찰을 진행합니다."]
   [:p "캠페인을 선택하시고 (모두 하셔도 좋습니다!), 최적화에 준수할 예산으로 설정하십시오."]])

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs]}))
