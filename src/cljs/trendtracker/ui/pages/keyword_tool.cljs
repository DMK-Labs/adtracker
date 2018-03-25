(ns trendtracker.ui.pages.keyword-tool
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [keechma.ui-component :as ui]
            [trendtracker.ui.components.pure.form-inputs
             :refer
             [controlled-textarea controlled-switch]]
            [keechma.toolbox.ui :refer [route> sub>]]))

(def form-item-params
  {:wrapper-col {:xs {:span 24}
                 :sm {:span 18}
                 :style {:max-width 400}}
   :label-col {:xs {:span 24}
               :sm {:span 6}}})

(defn render [ctx]
  (let [form-id [:keyword-tool :form]
        form-state @(forms-helpers/form-state ctx form-id)
        helpers (forms-helpers/make-component-helpers ctx form-id)
        submitting? (= (get-in form-state [:state :type]) :submitting)

        subpage (:subpage (route> ctx))
        data (sub> ctx :keyword-tool)]
    [:div
     [:div.content-header
      [(ui/component ctx :breadcrumbs)]
      [:h2 "키워드 관리"]
      [:p "입력 키워드 리스트 기반으로 경쟁도, 예상 수치를 탐색할 수 있는
      툴입니다. 새로운 블루오션 키워드를 발견하여 더 많은 잠재고객을 만나십시오."]
      [ant/alert {:message "모든 지표는 네이버 전체 시스템 내의 과거 28일(4주)의 성과를 비롯해 산출되었습니다."
                  :banner true
                  :closable true}]]

     [:div.content
      (if subpage
        [(ui/component ctx :keyword-tool-results-table)]
        [:div 
         [ant/row
          [ant/spin {:spinning submitting?}
           [ant/card {:title "키워드 조사"}
            [ant/form {:on-submit (:submit helpers)}

             [controlled-textarea
              {:form-state form-state
               :helpers helpers
               :placeholder "1줄에 1개씩, 키워드 내에는 빈 칸 또는 특수문자 없이."
               :attr :keywords
               :label "키워드 목록"
               :rows 4
               :default-value (:query data)}]

             [ant/form-item (assoc form-item-params :label "Excel 업로드")
              [ant/upload-dragger {:disabled true}
               [ant/icon {:type "upload" :style {:font-size 24}}]]]

             [controlled-switch
              {:form-state form-state
               :helpers helpers
               :attr :include-related?
               :label "연관어 포함"}]

             [ant/form-item (assoc form-item-params
                                   :wrapper-col {:xs {:span 24}
                                                 :sm {:span 18 :offset 6}}
                                   :style {:margin-bottom 0})
              [ant/button {:htmlType "submit"
                           :type "primary"}
               "키워드 검토"]]]]]]
         [ant/row [(ui/component ctx :keywords-list)]]])]]))

(def component
  (ui/constructor
    {:renderer render
     :component-deps [:breadcrumbs
                      :keyword-tool-results-table
                      :keywords-list]
     :topic forms-core/id-key
     :subscription-deps [:form-state
                         :keyword-tool]}))
