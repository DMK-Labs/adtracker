(ns trendtracker.ui.pages.optimize
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [trendtracker.ui.components.common :as common]))

(defn render [ctx]
  (let [bc (ui/component ctx :breadcrumbs)
        portfolio (ui/component ctx :portfolio)
        opt-settings (ui/component ctx :optimize-settings)]
    [:div
     [common/content-header
      [bc]
      [ant/row {:type "flex" :justify "space-between"}
       [ant/col
        [:h2.page-title "자동 입찰 최적화" common/beta]
        [:div.page-description {:style {:margin-right 8}}
         "캠페인의 입찰 전략과 예산 관리를 돕는 AI입니다."]]
       [ant/button {:icon "reload"
                    :onClick #(ant/notification-success
                               {:message "동기화가 요청 되었습니다"
                                :description (js/Date)
                                :placement "bottomRight"})}
        "네이버 시스템과 동기화"]]]
     [:div.content
      [ant/row {:gutter 16}
       [ant/col
        [opt-settings ctx]]
       [ant/col
        [ant/card [portfolio]]]]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs
                     :portfolio
                     :optimize-settings]}))

