(ns trendtracker.ui.pages.manage
  (:require [antizer.reagent :as ant]
            [clojure.set :as set]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  (let [stats (sub> ctx :segment-stats)]
    [:div
     [:div.content-header
      [:h2 "Smart Ad Manager"]]
     [:div.content
      [ant/card {:title "광고그룹별 성과"}
       [(ui/component ctx :scatter-chart)
        stats
        :ctr :impCnt :profit
        "클릭률" "노출수" "이윤"]]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:scatter-chart]
    :subscription-deps [:segment-stats]}))
