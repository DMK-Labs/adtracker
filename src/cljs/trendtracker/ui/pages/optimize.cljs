(ns trendtracker.ui.pages.optimize
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> route>]]
            [trendtracker.utils :as u]))

(defn new-opt-btn [ctx]
  [:a {:href (ui/url ctx (assoc (route> ctx)
                                :page "optimize"
                                :subpage "new"
                                :step "1"))}
   [ant/button {:type "primary" :icon "setting"}
    "자동 최적화 설정"]])

(defn opt-settings [ctx]
  (let [settings (sub> ctx :optimize-settings)
        {:keys [budget objective clicks cost]} settings]
    [:ul.opt-settings
     [:li.opt-setting {:style {:border-right "1px solid #e6ebf1"}}
      [:h2 (u/krw budget)] [:p "월 예산"]]
     [:li.opt-setting {:style {:border-right "1px solid #e6ebf1"}}
      [:h2 (u/int-fmt clicks)] [:p "예상 클릭"]]
     [:li.opt-setting
      [:h2 (u/krw (int (/ budget clicks)))] [:p "예상 CPC"]]]))

(defn render [ctx]
  [:div
   [:div.content-header
    [(ui/component ctx :breadcrumbs)]
    [ant/row ;; {:type "flex" :align "center"}
     [ant/col ;; {:md 14 :xs 24}
      [:h2 "자동 입찰 최적화"]
      [:p {:style {:margin-right 8}}
       "관리중인 키워드를 자동 실시간 관리할 수 있는 AI 기반 입찰 툴입니다. 캠페인을 선택하시고 (모두 하셔도 좋습니다!), 최적화에 준수할 예산으로 설정하십시오."]]
     ;; [ant/col {:md 10 :xs 24}
     ;;  [opt-settings ctx]]
     ]
    [new-opt-btn ctx]]
   [:div.content
    [ant/card
     [ant/row
      [ant/col
       [(ui/component ctx :portfolio)]]]]]])

(def component
  (ui/constructor
    {:renderer render
     :component-deps [:breadcrumbs
                      :portfolio]
     :subscription-deps [:optimize-settings]}))
