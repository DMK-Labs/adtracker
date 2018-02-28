(ns trendtracker.ui.pages.dashboard
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> route>]]
            [reagent.core :as r]
            [trendtracker.ui.components.common :as common]))

(defn render [ctx]
  (let [breadcrumbs (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)
        snapshot (ui/component ctx :snapshot)
        tabbed-charts (ui/component ctx :tabbed-charts)
        cascader (ui/component ctx :cascader)
        daily-stats (sub> ctx :daily-stats)
        segment-stats (ui/component ctx :segment-stats)
        no-conversions? (every? zero? (concat
                                       (map :conversions (:curr daily-stats))
                                       (map :conversions (:prev daily-stats))))]
    [:div
     [:div.content-header
      [breadcrumbs]
      [ant/row {:gutter 16 :type "flex" :justify "space-between"}
       [ant/col {:md 7 :xs 24}
        [:h2 common/naver-icon "검색광고 대쉬보드"]]
       [ant/col
        [ant/row {:type "flex" :justify "space-between" :gutter 8}
         [ant/col [cascader]]
         [ant/col [date-range-picker]]]]]]

     ;; [(ui/component ctx :summary)]

     (if (empty? (:curr daily-stats))
       [:div.content
        [ant/alert
         {:showIcon true
          :message "광고 성과 정보가 없습니다. "
          :description (r/as-element
                        [:span [:p "이 기간에 집행된 광고가 없거나, 아직 정보가 동기화 중입니다. 다른 광고 계정을 선택하시거나, 문제가 지속된다면 관리자에게 문의해 주십시오."]
                         [:a {:href (str "https://manage.searchad.naver.com/customers/" (:client (route> ctx)) "/campaigns")
                              :target "_blank"}
                          "네이버에서 확인하기"]])
          :type "warning"}]
        [:img {:src "/img/404.svg"
               :style {:display "block"
                       :margin-top 32
                       :margin-left "auto"
                       :margin-right "auto"}}]]
       [:div.content
        (if no-conversions?
          [ant/row {:gutter 16}
           [ant/col {:md 8 :sm 24} [snapshot :budget]]
           [ant/col {:md 8 :sm 12} [snapshot :cpc]]
           [ant/col {:md 8 :sm 12} [snapshot :cpm]]]
          [:div
           [ant/row {:gutter 16}
            [ant/col {:sm 8} [snapshot :budget]]
            [ant/col {:sm 8} [snapshot :revenue]]
            [ant/col {:sm 8} [snapshot :roas]]]
           [ant/row {:gutter 16}
            [ant/col {:sm 8} [snapshot :cpc]]
            [ant/col {:sm 8} [snapshot :cpm]]
            [ant/col {:sm 8} [snapshot :cpa]]]])
        [ant/row {:gutter 16}
         [ant/col {:lg 24} [tabbed-charts]]
         [ant/col {:lg 24} [(ui/component ctx :device-pie)]]]
        [ant/row [ant/col [segment-stats]]]])]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:date-range-picker
                     :breadcrumbs
                     :snapshot
                     :tabbed-charts
                     :segment-stats
                     :cascader
                     ;; :summary
                     :device-pie
                     ]
    :subscription-deps [:daily-stats]}))
