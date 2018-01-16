(ns trendtracker.ui.components.cascader
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [<cmd sub>]]))

(def portfolio-tree
  [{:value :total :label "모든 검색광고"}
   {:value :powerlink :label "파워링크"
    :children [{:value "cmp-a001-01-000000000283508" :label "대쉬크랩_스토어팜"}
               {:value "cmp-a001-01-000000000285504" :label "대쉬크랩_스토어팜_M"}]}
   {:value :shopping :label "쇼핑"
    :children [{:value "cmp-a001-02-000000000875838" :label "써클 쇼핑검색"}
               {:value "cmp-a001-02-000000000876295" :label "써클 통합검색"}
               {:value "cmp-a001-02-000000000900728" :label "거치대 쇼핑검색"}
               {:value "cmp-a001-02-000000000161838" :label "네이버쇼핑"}
               {:value "cmp-a001-02-000000000847146" :label "쇼핑검색"}
               {:value "cmp-a001-02-000000000848957" :label "통합검색"}]}
   {:value :brand :label "브랜드"
    :disabled true
    :children [{:value "cmp-a001-04-000000000737287" :label "브랜드검색"}]}])

(defn cascade-render
  ([ctx] (cascade-render ctx nil))
  ([ctx opts]
   [:div [ant/icon {:type "filter"}]
    [ant/cascader
     (merge {:style {:min-width 200
                     :margin-left 8}
             :value (sub> ctx :cascader)
             :changeOnSelect true
             :onChange #(<cmd ctx :set (js->clj %))
             :options portfolio-tree}
            opts)]]))

(def component
  (ui/constructor
   {:renderer cascade-render
    :topic :cascader
    :subscription-deps [:cascader]}))
