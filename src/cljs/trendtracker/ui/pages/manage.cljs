(ns trendtracker.ui.pages.manage
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [reagent.core :as r]
            [trendtracker.ui.components.common :as common]))

(defn render [ctx]
  (let [breadcrumbs (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)
        scatter-chart (ui/component ctx :scatter-chart)
        todo-card (ui/component ctx :todo-card)]
    [:div
     [common/content-header
      [breadcrumbs]
      [ant/row {:type "flex" :justify "space-between"}
       [:h2.page-title "Smart To-Do" common/beta]
       [:div [date-range-picker]]]
      [:div.page-description "하루 20분이어도 OK! 가장 큰 효과를 기대할 수 있는 개선사항들을 찝어드립니다."]]
     [:div.content
      [ant/row
       [ant/card {:title "광고그룹별 성과"} [scatter-chart]]]
      [ant/row {:gutter 16}
       [ant/col {:md 12}
        (todo-card {:title "성공적인 광고 소재 검토"
                    :color "#13c2c2"
                    :description "역사적으로 가장 클릭률이 좋았던 광고, 키워드 콤보를 검토해 보세요."
                    :url-params {:subpage "best-ads"}})
        (todo-card {:title "이윤이 남지 않는 광고"
                    :color "#ff4d4f"
                    :description "발생 매출 - 비용이 음수인 경우. 버는 것 보다 소진되는 것이 많다는
                                  것입니다. 광고 전반적으로 비효율이 있을 가능성이 높습니다."
                    :todos [:ul
                            [:li [:a "의심되는 키워드 연관성 검토하기"]]
                            [:li [:a "효력 낮은 광고소재 검토하기"]]]})
        (todo-card {:title "전환 비용 효율의 검토가 필요한 그룹"
                    :color "#1890ff"
                    :description "평균보다 클릭률, 노출이 모두 높으나 Profit이 적은 경우는, 구매 전환에
                                  있어서 비용적 효율이 낮을 확률이 높습니다. 노출, 클릭, 전환 각 단계에서 단위
                                  비용 절약을 시도해 보십시오."
                    :todos [:ul
                            [:li [:a "CPM 검토하기"]]
                            [:li [:a "CPC 검토하기"]]
                            [:li [:a "CPA 검토하기"]]]})]
       [ant/col {:md 12}
        (todo-card {:title "노출 대비 클릭률이 낮은 소재"
                    :color "#fa8c16"
                    :description "노출이 제대로 이루어지고 있으나 클릭이 발생하지 않는다는 것은 광고 소재가
                                  충분히 호소력이 없거나, 광고 의도와 키워드 검색 의도의 alignment가
                                  부족하단 것입니다."
                    :todos [:ul
                            [:li [:a "의심되는 키워드 연관성 검토하기"]]
                            [:li [:a "효력 낮은 광고소재 검토하기"]]]})
        (todo-card {:title "클릭률 대비 전환매출이 적은 그룹"
                    :color "#52c41a"
                    :description "클릭이 잘 이루어진다는 것은 고객의 관심과 의도가 존재한다는 것입니다. 이렇게
                                  클릭해서 들어온 고객을 전환시키지 못하는 것은 추후 Pipeline에서 문제가
                                  있을 확률이 높습니다."
                    :todos [:ul
                            [:li [:a "Landing Page 검토하기"]]
                            [:li [:a "Call-to-Action 검토하기"]]
                            [:li [:a "결제, 구매 과정 검토하기"]]]})]]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:scatter-chart
                     :breadcrumbs
                     :date-range-picker
                     :todo-card]}))
