(ns trendtracker.ui.pages.keyword-tool
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [:div.content-header
   [(ui/component ctx :breadcrumbs)]
   [:h1 "키워드 도구"]
   [:p "입력 키워드 리스트를 기반으로 키워드, 경쟁도, 예상 수치를 탐색할 수 있는 툴입니다."]])

[ant/card
 (ant/create-form
  (fn [_]
    [ant/form
     [ant/form-item {:label "키워드 목록 - 한줄에 하나씩"}
      [ant/input-text-area]]
     [ant/form-item
      [ant/button "추천 키워드 확인"]]])
  {:options
   {:onFieldsChange #(.log js/console %)
    :mapPropsToFields (fn [props]
                        {:keywords ((.. js/antd -Form -createFormField)
                                    props)})
    :onValuesChange (fn [& args] (.log js/console args))}})]

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs]}))
