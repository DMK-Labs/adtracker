(ns trendtracker.ui.components.optimize.objective
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.ui :refer [sub> route>]]
            [keechma.ui-component :as ui]
            [trendtracker.ui.components.pure.form-inputs :refer [controlled-radio-group]]
            [keechma.toolbox.forms.ui :as forms-ui]))

(defn step-1 [ctx]
  (let [optimizing (sub> ctx :portfolio-optimizing)
        form-props [:optimize-objective :form]
        form-state (forms-ui/form-state> ctx form-props)]
    [ant/card
     [ant/alert {:message "자동 입찰 최적화는 조정할 수 있는 변수가 많을수록 더 좋은 결과를 드릴 수 있습니다. 되도록이면 모든 광고그룹을 일괄적으로 관리할 수 있도록 한번에 최적화 적용하는 것을 추천드립니다."
                 :style {:margin-bottom 32}
                 :banner true}]
     [ant/spin {:spinning (empty? optimizing)}
      [ant/form {:on-submit #(forms-ui/<submit ctx form-props)}
       [controlled-radio-group ctx form-props :objective
        {:label "최적화 기준"
         :options [{:value :impressions :label "노출"}
                   {:value :clicks :label "클릭"}
                   {:value :conversions :label "전환"}]}]
       [ant/form-item {:style {:margin-bottom 0}}
        [ant/button-group
         [ant/button {:on-click #(ui/redirect ctx {:page "optimize"
                                                   :client (:client (route> ctx))})}
          "취소"]
         [ant/button {:type "primary"                       ;; :htmlType "submit"
                      :on-click #(ui/redirect ctx {:page "optimize"
                                                   :subpage "settings"
                                                   :step 2
                                                   :client (:client (route> ctx))})}
          "예산 설정" [ant/icon {:type "right"}]]]]]]]))

(def component
  (ui/constructor
   {:renderer step-1
    :subscription-deps [:portfolio-optimizing]}))
