(ns trendtracker.ui.components.optimize.objective
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [trendtracker.ui.components.pure.form-inputs
             :refer
             [controlled-tree-select
              controlled-radio-group]]
            [json-html.core :as jh]))

(defn remove-children [m]
  (dissoc m :children))

(defn remove-grandchildren [m]
  (update m :children #(map remove-children %)))

(defn step-1 [ctx]
  (let [powerlink (->> (sub> ctx :portfolio)
                       (filter #(= "powerlink"
                                   (:value (js->clj % :keywordize-keys true))))
                       (map remove-grandchildren))
        form-id [:optimize-objective :form]
        form-state @(forms-helpers/form-state ctx form-id)
        helpers (forms-helpers/make-component-helpers ctx form-id)
        submitting? (= (get-in form-state [:state :type]) :submitting)]
    [ant/card
     [ant/alert {:message "자동 입찰 최적화는 조정할 수 있는 변수가 많을수록 더 좋은 결과를 드릴 수 있습니다. 되도록이면 모든 광고그룹을 일괄적으로 관리할 수 있도록 한번에 최적화 적용하는 것을 추천드립니다."
                 :style {:margin-bottom 32}
                 :banner true}]
     [ant/form {:on-submit (:submit helpers)}
      [controlled-tree-select
       {:form-state           form-state
        :helpers              helpers
        :attr                 :targets
        :label                "최적화 대상 캠페인 선택"
        :treeCheckable        true
        :treeDefaultExpandAll true
        :treeData             powerlink
        :placeholder          "입찰 최적화 대상을 서택하십시오."}]
      [controlled-radio-group
       {:form-state form-state
        :helpers    helpers
        :attr       :objective
        :label      "최적화 기준"
        :options    [{:value :impressions :label "노출"}
                     {:value :clicks :label "클릭"}
                     {:value :conversions :label "전환" :disabled true}
                     {:value :profit :label "광고이익" :disabled true}]}]
      [ant/form-item {:style {:margin-bottom 0}}
       [ant/button-group
        [ant/button {:on-click #(ui/redirect ctx {:page "optimize"})} "취소"]
        [ant/button {:type "primary" :htmlType "submit"}
         "다음" [ant/icon {:type "right"}]]]]]
     [:div (jh/edn->hiccup form-state)]]))

(def component
  (ui/constructor
    {:renderer step-1
     :topic forms-core/id-key
     :subscription-deps [:form-state
                         :portfolio]}))
