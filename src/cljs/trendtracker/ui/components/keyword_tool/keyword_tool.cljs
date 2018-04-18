(ns trendtracker.ui.components.keyword-tool.keyword-tool
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [trendtracker.ui.components.pure.form-inputs :refer [controlled-textarea
                                                                 controlled-switch]]
            [keechma.toolbox.ui :refer [route> sub>]]
            [keechma.toolbox.forms.ui :as forms-ui]))

(defn render [ctx]
  (let [form-props [:keyword-tool :form]
        form-state (forms-ui/form-state> ctx form-props)
        submitting? (= (get-in form-state [:state :type]) :submitting)
        data (sub> ctx :keyword-tool)
        route (route> ctx)]
    [:div
     [:h3 "신규 키워드 검토"]
     [:p "성과가 궁금한 키워드 아래에 입력해 보십시오."]
     [ant/form {:onSubmit #(forms-ui/<submit ctx form-props %)
                :layout "vertical"}
      [controlled-textarea ctx form-props :keywords {:placeholder "1줄에 1개씩, 키워드 내에는 빈 칸 또는 특수문자 없이."
                                                     ;; :label "키워드 목록"
                                                     :rows 5
                                                     :default-value (:query data)}]
      [controlled-switch ctx form-props :include-related? {:label "연관어 포함"}]
      [ant/button
       {:htmlType "submit"
        :type "primary"
        :style {:margin-right 8}
        :loading submitting?}
       "견적 산출"]
      [ant/button
       {:onClick #(ui/redirect ctx (dissoc route :subpage :result))}
       "취소"]]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:keyword-tool]}))
