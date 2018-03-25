(ns trendtracker.ui.pages.manage
  (:require [antizer.reagent :as ant]
            [clojure.set :as set]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [reagent.core :as r]))

(defn todo-card [{:keys [title color description todos]}]
  [ant/card {:title (r/as-element [:div [ant/icon {:type "bulb" :style {:margin-right 8 :color color}}] title])
             :extra (r/as-element [ant/icon {:type "info-circle-o"}])}
   [:p description]
   todos
   [ant/button "확인하기"]])

(defn render [ctx]
  (let [stats (sub> ctx :segment-stats)
        breadcrumbs (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)]
    [:div
     [:div.content-header
      [breadcrumbs]
      [ant/row {:gutter 16 :type "flex" :justify "space-between"}
       [:h2 "Smart To-Do "
        [:span {:style {:background-color "gainsboro" :font-size 14}}
         "BETA"]]
       [:div [date-range-picker]]]
      [:div "하루 20분이어도 OK! 가장 큰 효과를 기대할 수 있는 개선사항들을 찝어드립니다."]]
     [:div.content
      [ant/row
       [ant/card {:title "광고그룹별 성과"}
        [(ui/component ctx :scatter-chart) {:data stats
                                            :x :ctr
                                            :y :impressions
                                            :z :profit
                                            :x-label "클릭률"
                                            :y-label "노출수"
                                            :z-label "이윤"}]]]
      [ant/row {:gutter 16}
       [ant/col {:span 12}
        (todo-card {:title "클릭률 대비 전환매출이 적은 그룹"
                    :color "#52c41a"
                    :description "클릭이 잘 이루어진다는 것은 고객의 관심과 의도가 존재한다는
         것입니다. 이렇게 클릭해서 들어온 고객을 전환시키지 못하는 것은 추후
         Pipeline에서 문제가 있을 확률이 높습니다."
                    :todos [:ul
                            [:li [:a "Landing Page 검토하기"]]
                            [:li [:a "Call-to-Action 검토하기"]]
                            [:li [:a "결제, 구매 과정 검토하기"]]]})]
       [ant/col {:span 12}
        (todo-card {:title "전환 비용 효율의 검토가 필요한 그룹"
                    :color "#1890ff"
                    :description "평균보다 클릭률, 노출이 모두 높으나 Profit이 적은 경우는, 구매
         전환에 있어서 비용적 효율이 낮을 확률이 높습니다. 노출, 클릭, 전환 각
         단계에서 단위 비용 절약을 시도해 보십시오."
                    :todos [:ul
                            [:li [:a "CPM 검토하기"]]
                            [:li [:a "CPC 검토하기"]]
                            [:li [:a "CPA 검토하기"]]]})]
       [ant/col {:span 12}
        (todo-card {:title "노출 대비 클릭률이 낮은 소재"
                    :color "#fa8c16"
                    :description "노출이 제대로 이루어지고 있으나 클릭이 발생하지 않는다는 것은
         광고 소재가 충분히 호소력이 없거나, 광고 의도와 키워드 검색 의도의
         alignment가 부족하단 것입니다."
                    :todos [:ul
                            [:li [:a "의심 키워드 연관성 검토하기"]]
                            [:li [:a "효력 낮은 광고소재 검토하기"]]]})]
       #_[ant/col {:span 12}
          (todo-card {:title "마이너스 이윤 그룹"
                      :color "grey"
                      :description "돈을 잃고 있는 광고그룹. 비용이 높다는 것은
                    클릭이 잘 이루어진다는 것--이러면서 돈을 잃는것은 전환을
                    유도하지 못한다는 것입니다. 전환률을 개선하면 좋을 수
                    있습니다."
                      :todos [:ul
                              [:li [:a "검색의도와 제공 Landing Page 검토하기"]]]})]]]]))


(def component
  (ui/constructor
   {:renderer render
    :component-deps [:scatter-chart
                     :breadcrumbs
                     :date-range-picker]
    :subscription-deps [:segment-stats]}))
