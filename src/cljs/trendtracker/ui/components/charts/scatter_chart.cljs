(ns trendtracker.ui.components.charts.scatter-chart
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]
            [keechma.toolbox.ui :refer [sub>]]
            [reagent.core :as r]))

(defn render [ctx]
  (let [data (sub> ctx :aggregate-stats)
        scale "sqrt"]
    [ant/spin {:spinning (empty? data)}
     [ant/card {:title "클릭률 vs. 노출수"}
      [recharts/responsive-container {:height 240}
       [recharts/scatter-chart
        [recharts/tooltip]
        [recharts/x-axis {:dataKey :ctr :scale scale :type "number"}
         [recharts/label {:value "Clickthrough" :position "insideBottom" :offset 0}]]
        [recharts/y-axis {:dataKey :impCnt :scale scale}
         [recharts/label {:value "Impressions" :position "insideLeft" :offset 0 :angle -90}]]
        [recharts/z-axis {:dataKey :salesAmt :range [30 600]}]
        [recharts/scatter
         {:isAnimationActive true
          :data data
          :stroke "#1890ff" :fill "#bae7ff"}]]]]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:aggregate-stats]}))
