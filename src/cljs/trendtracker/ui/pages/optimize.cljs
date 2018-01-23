(ns trendtracker.ui.pages.optimize
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn new-opt-btn [ctx]
  [:a {:href (ui/url ctx {:page "optimize" :subpage "new" :step "1"})}
   [ant/button {:type "primary" :icon "setting"}
    "자동 최적화 설정"]])

(defn render [ctx]
  (let [style {:style {:background-color "#d7ef85"}}]
    [:div
     [:div.content-header
      [(ui/component ctx :breadcrumbs)]
      [:h1 "자동 입찰 최적화"]
      [:p "관리중인 키워드를 자동 실시간 관리할 수 있는 AI 기반 입찰 툴입니다. 캠페인을 선택하시고 (모두 하셔도 좋습니다!), 최적화에 준수할 예산으로 설정하십시오."]]
     [:div.content
      [ant/card
       [ant/row
        [:p "현재 "
         [:b style "2개"]
         " 캠페인 대상으로, 월 "
         [:b style "13,239,000원"]
         " 예산 하에 "
         [:b style "클릭수"]
         "를 극대화 하는 최적화 진행 중."]]
       [ant/row {:style {:margin-bottom 16}}
        [ant/col [new-opt-btn ctx]]]
       [ant/row
        [ant/col
         [(ui/component ctx :portfolio)]]]]]]))

(def component
  (ui/constructor
    {:renderer render
     :component-deps [:breadcrumbs
                      :portfolio]}))
