(ns trendtracker.ui.pages.overview
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [trendtracker.ui.components.common :as common]))

(defn render [ctx]
  [:div
   [common/content-header
    [:h2.page-title "Ad Tracker에 오신 것을 환영합니다."]]
   [:div.content
    [ant/card
     [:ol
      [:li "계정과 연결"]
      [:li "광고 현황 확인"]
      [:li "입찰 최적화 실시"]]]]])

(def component
  (ui/constructor
   {:renderer render}))
