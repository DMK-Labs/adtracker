(ns trendtracker.ui.components.charts.scatter
  (:require [reacharts.recharts :as recharts]
            [keechma.ui-component :as ui]))

(defn scatter-chart [ctx data x y x-label y-label]
  [recharts/responsive-container {:height 300}
   [recharts/scatter-chart
    [recharts/tooltip]
    [recharts/x-axis {:dataKey x :scale :sqrt :type "number"}
     [recharts/label {:value x-label :position "insideBottom" :offset 0}]]
    [recharts/y-axis {:dataKey y :scale :sqrt}
     [recharts/label {:value y-label :position "insideLeft" :offset 0 :angle -90}]]
    [recharts/z-axis {:dataKey :profit :range [20 800]}]
    ;; [recharts/scatter
    ;;  {:isAnimationActive false
    ;;   :data data
    ;;   :stroke "#1890ff" :fill "#bae7ff"
    ;;   :onClick #()}]
    [recharts/scatter
     {:isAnimationActive false
      :data data
      :stroke "#fa8c16" :fill "#ffe7ba"
      :onClick #()}]]])

(def component
  (ui/constructor
   {:renderer scatter-chart}))
