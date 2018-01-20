(ns trendtracker.ui.components.charts.conversion-funnel
  (:require [reacharts.recharts :as recharts]
            [keechma.ui-component :as ui]))

(defn conversion-funnel [ctx data]
  [recharts/responsive-container {:height 250}
   [recharts/area-chart {:data data}
    [recharts/x-axis {:dataKey :name}]
    [recharts/y-axis]
    [recharts/tooltip]
    [recharts/legend]
    [recharts/area {:dataKey     :to-be
                    :type        "monotone"
                    :fillOpacity 0.33
                    :stroke      "#87d3ff"
                    :fill        "#87d3ff"}]
    [recharts/area {:dataKey     :as-is
                    :type        "monotone"
                    :fillOpacity 0.33
                    :stroke      "#c7de85"
                    :fill        "#c7de85"}]]])

(def component
  (ui/constructor
   {:renderer conversion-funnel}))
