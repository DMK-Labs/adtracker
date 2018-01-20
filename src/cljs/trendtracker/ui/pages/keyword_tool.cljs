(ns trendtracker.ui.pages.keyword-tool
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.forms.core :as forms-core]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [keechma.ui-component :as ui]
            [trendtracker.ui.components.pure.form-inputs
             :refer
             [controlled-textarea]]
            [keechma.toolbox.ui :refer [sub>]]))

(defn render [ctx]
  (let [form-id [:keyword-tool :form]
        form-state @(forms-helpers/form-state ctx form-id)
        helpers (forms-helpers/make-component-helpers ctx form-id)]
    [:div
     [:div.content-header
      [(ui/component ctx :breadcrumbs)]
      [:h1 "키워드 도구"]
      [:p "입력 키워드 리스트를 기반으로 키워드, 경쟁도, 예상 수치를 탐색할 수 있는 툴입니다."]]
     [:div.content
      [ant/row
       [ant/card
        [ant/form {:on-submit (:submit helpers)}
         [controlled-textarea
          {:form-state form-state
           :helpers helpers
           :placeholder "Keyword List"
           :attr :keywords
           :label "키워드 (1줄에 1개)"
           :rows 5}]
         [ant/form-item {:wrapper-col {:xs {:span 24}
                                       :sm {:span 18 :offset 6}}
                         :style {:margin-bottom 0}}
          [ant/button {:htmlType "submit"}
           "추천 키워드 확인"]]]]]
      (when-let [data (sub> ctx :keyword-tool)]
        [ant/row
         [ant/card
          [:code (pr-str data)]]])]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs]
    :topic forms-core/id-key
    :subscription-deps [:form-state
                        :keyword-tool]}))
