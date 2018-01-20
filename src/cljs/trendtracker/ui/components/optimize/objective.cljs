(ns trendtracker.ui.components.optimize.objective
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn step-1 []
  (let [w-layout #(merge {:labelCol {:span 6}
                          :wrapperCol {:span 18}}
                         %)]
    [:div
     [:p "최적화를 진행할 대상 캠페인 또는 광고그룹을 선택하시오."]
     [:p "자동 입찰 최적화는 조정할 수 있는 변수가 많을수록 더 좋은 결과를 드릴 수 있습니다. 되도록이면 모든 광고그룹을 일괄적으로 관리할 수 있도록 한번에 최적화 적용하는 것을 추천드립니다."]
     [:br]
     [ant/form
      [ant/form-item (w-layout {:label "최적화 기준: "})
       [ant/radio-group {:defaultValue :imp}
        [ant/radio-button {:value :imp} "Impressions"]
        [ant/radio-button {:value :clk} "Clicks"]
        [ant/radio-button {:value :conv} "Conversions"]
        [ant/radio-button {:value :profit :disabled true} "Profit"]]]
      [ant/form-item (w-layout {:label "최적화 대상 캠페인 선택: "})
       [ant/tree-select]]]]))

(def component
  (ui/constructor
    {:renderer step-1}))
