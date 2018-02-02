(ns trendtracker.ui.pages.login
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.forms.helpers :as forms-helpers]
            [keechma.toolbox.forms.core :as forms-core]
            [trendtracker.ui.components.pure.form-inputs :refer [controlled-input]]))

(defn render [ctx]
  (let [form-id [:login :form]
        form-state @(forms-helpers/form-state ctx form-id)
        helpers (forms-helpers/make-component-helpers ctx form-id)
        submitting? (= (get-in form-state [:state :type]) :submitting)]
    [:div {:style {:display "flex"
                   :justify-content "center"
                   :align-items "center"
                   :height "100vh"}}
     [ant/spin {:spinning submitting?}
      [ant/card {:style {:padding 32}}
       [:h1 "DataMKTKorea Ad Tracker"]
       [:h4 "모든 마케팅 ROI를 정량화 한다!"]
       [:br]
       [ant/form {:on-submit (:submit helpers)}
        [controlled-input {:form-state form-state
                           :helpers helpers
                           :attr :email
                           :label "이메일"
                           :placeholder "hong@gildong.co.kr"}]
        [controlled-input {:form-state form-state
                           :helpers helpers
                           :attr :password
                           :label "비밀번호"
                           :input-type :password}]
        [ant/form-item {:wrapperCol {:xs {:span 24} :sm {:span 18 :offset 6}}}
         [ant/button {:htmlType "submit" :type "primary"}
          "로그인"]]]]]]))

(def component
  (ui/constructor
   {:renderer render
    :topic forms-core/id-key
    :subscription-deps [:form-state]}))
