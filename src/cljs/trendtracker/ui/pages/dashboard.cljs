(ns trendtracker.ui.pages.dashboard
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub>]]))

(defn render [ctx]
  (let [breadcrumbs       (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)
        snapshot          (ui/component ctx :snapshot)
        tabbed-charts     (ui/component ctx :tabbed-charts)
        cascader          (ui/component ctx :cascader)
        daily-stats       (sub> ctx :daily-stats)]
    [:div
     [:div.content-header
      [breadcrumbs]
      [ant/row {:gutter 16 :type "flex" :justify "space-between"}
       [ant/col {:md 6 :xs 24}
        [:h2 "검색광고 대쉬보드"]]
       [ant/col
        [ant/row {:type "flex" :justify "space-between" :gutter 16}
         [ant/col [cascader]]
         [ant/col [date-range-picker]]]]]]

     (if (empty? (:curr daily-stats))
       [:div.content
        [ant/alert
         {:showIcon true
          :message "광고 성과 정보가 없습니다. "
          :description "이 기간에 집행된 광고가 없거나, 아직 정보가 동기화 중입니다. 다른 광고 계정을 선택하시거나, 문제가 지속된다면 관리자에게 문의해 주십시오."
          :type "warning"}]]
       [:div.content
        [ant/row {:gutter 16}
         [ant/col {:md 8 :sm 12} [snapshot :cost]]
         [ant/col {:md 8 :sm 12} [snapshot :revenue]]
         [ant/col {:md 8 :sm 12} [snapshot :roas]]]
        [ant/row [tabbed-charts]]
        [ant/row {:gutter 16}
         [ant/col {:md 8 :sm 12} [snapshot :cpc]]
         [ant/col {:md 8 :sm 12} [snapshot :cpm]]
         [ant/col {:md 8 :sm 12} [snapshot :cpa]]]])]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:date-range-picker
                     :breadcrumbs
                     :snapshot
                     :tabbed-charts
                     :cascader]
    :subscription-deps [:daily-stats]}))
