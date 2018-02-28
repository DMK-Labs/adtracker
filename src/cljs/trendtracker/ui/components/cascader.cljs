(ns trendtracker.ui.components.cascader
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [<cmd sub>]]
            [reagent.core :as r]))

(defn cascade-render
  ([ctx] (cascade-render ctx nil))
  ([ctx opts]
   (let [portfolio (sub> ctx :portfolio)]
     [ant/spin {:spinning (empty? portfolio)
                :size "small"}
      [:div [ant/icon {:type "filter"}]
       [ant/cascader
        (merge {:style {:margin-left 8}
                :value (sub> ctx :cascader)
                :changeOnSelect true
                :onChange #(<cmd ctx :set (js->clj %))
                :options portfolio
                :allowClear false}
               opts)]]])))

(def component
  (ui/constructor
   {:renderer cascade-render
    :topic :cascader
    :subscription-deps [:cascader
                        :portfolio]}))
