(ns trendtracker.ui.components.optimize.objective
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [keechma.ui-component :as ui]))

(def tree
  [{:label "파워링크"
    :value :powerlink
    :key :powerlink
    :children [{:key "cmp-a001-01-000000000283508"
                :value "cmp-a001-01-000000000283508"
                :label "대쉬크랩_스토어팜"}
               {:key "cmp-a001-01-000000000285504"
                :value "cmp-a001-01-000000000285504"
                :label "대쉬크랩_스토어팜_M"}]}
   #_{:label "쇼핑"
    :value :shopping
    :key :shopping
    :disabled true}
   #_{:label "브랜드"
    :value :brand
    :key :brand
    :disabled true}])

(defn step-1 [ctx]
  (let [w-layout #(merge {:labelCol {:span 6}
                          :wrapperCol {:span 18}}
                         %)]
    [ant/card
     [ant/alert {:message "자동 입찰 최적화는 조정할 수 있는 변수가 많을수록 더 좋은 결과를 드릴 수 있습니다. 되도록이면 모든 광고그룹을 일괄적으로 관리할 수 있도록 한번에 최적화 적용하는 것을 추천드립니다."}]
     [:br]
     [ant/form
      [ant/form-item (w-layout {:label "최적화 기준: "})
       [ant/radio-group {:defaultValue :imp}
        [ant/radio-button {:value :imp} "노출"]
        [ant/radio-button {:value :clk} "클릭"]
        [ant/radio-button {:value :conv} "전환"]
        [ant/radio-button {:value :profit :disabled true} "광고이익"]]]
      [ant/form-item (w-layout {:label "최적화 대상 캠페인 선택: "})
       [ant/tree-select
        {:treeCheckable true
         :treeDefaultExpandAll true
         :treeData tree
         :defaultValue ["cmp-a001-01-000000000285504" "cmp-a001-01-000000000283508"]
         :style {:max-width 500}}]]
      [ant/form-item {:style {:margin-bottom 0}}
       [ant/button-group
        [ant/button {:on-click #(ui/redirect ctx {:page "optimize"})}
         "취소"]
        [ant/button
         {:on-click #(ui/redirect ctx {:page "optimize" :subpage "new" :step 2})
          :type "primary"}
         "다음" [ant/icon {:type "right"}]]]]]]))

(def component
  (ui/constructor
    {:renderer step-1
     :topic forms-core/id-key
     :subscription-deps [:form-state]}))
