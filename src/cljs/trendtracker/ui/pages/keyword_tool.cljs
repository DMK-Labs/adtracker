(ns trendtracker.ui.pages.keyword-tool
  (:require [antizer.reagent :as ant]
            [forms.core :as f]
            [forms.validator :as v]
            [keechma.ui-component :as ui]))

(def validations
  {:not-empty {:message "키워드 1개 이상을 입력하시오."}})

(defn to-validator [validations config]
  (reduce-kv (fn [m attr v]
               (assoc m attr
                      (map (fn [k] [k (get-in validations [k :validator])])
                           v)))
             {} config))

(def validator
  (v/validator
   (to-validator validations
                 {:keyword-list [:not-empty]})))

(def form
  (f/constructor validator))

(def inited-form
  (form {}))

(defn render [ctx]
  [:div
   [:div.content-header
    [(ui/component ctx :breadcrumbs)]
    [:h1 "키워드 도구"]
    [:p "입력 키워드 리스트를 기반으로 키워드, 경쟁도, 예상 수치를 탐색할 수 있는 툴입니다."]]

   [:div.content
    [ant/card
     [ant/form
      [ant/form-item {:label "키워드 목록 - 한줄에 하나씩"}
       [ant/input-text-area]]
      [ant/form-item
       [ant/button "추천 키워드 확인"]]]]]])

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs]}))
