(ns trendtracker.ui.pages.optimize
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [trendtracker.ui.components.common :as common]))

(defn render [ctx]
  (let [bc (ui/component ctx :breadcrumbs)
        portfolio (ui/component ctx :portfolio)
        opt-settings (ui/component ctx :optimize-settings)]
    [:div
     [:div.content-header
      [bc]
      [ant/row
       [ant/col
        [:h2 common/naver-icon "자동 입찰 최적화"]
        [:p {:style {:margin-right 8}}
         "관리중인 키워드를 자동 실시간 관리할 수 있는 AI 기반 입찰 툴입니다. 캠페인을 선택하시고 (모두 하셔도 좋습니다!), 최적화에 준수할 예산으로 설정하십시오."]]]
      [ant/button {:icon "reload"
                   :onClick #(ant/notification-success
                              {:message "동기화가 요청 되었습니다"
                               :description (js/Date)
                               :placement "bottomRight"})}
       "네이버 시스템과 동기화 요청"]]
     [:div.content
      [ant/row {:gutter 16}
       [ant/col
        [opt-settings ctx]]
       [ant/col
        [ant/card
         [portfolio]]]]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs
                     :portfolio
                     :optimize-settings]}))

