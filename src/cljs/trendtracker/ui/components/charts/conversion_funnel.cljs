(ns trendtracker.ui.components.charts.conversion-funnel
  (:require [reacharts.recharts :as recharts]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub>]]
            [trendtracker.utils :as u]))

(defn conversion-funnel [ctx]
  (let [stats (sub> ctx :optimize-stats)
        ridgeline (sub> ctx :ridgeline)
        data [{:name "노출수" :expected (:impressions stats)}
              {:name "클릭수" :expected (:clicks stats)}
              {:name "전환수" :expected (:conversions stats)}]
        max (:impressions (last ridgeline))]
    [recharts/responsive-container {:height 200}
     [recharts/composed-chart {:data data
                               :barCategoryGap "20%"}
      [recharts/x-axis {:dataKey :name}]
      [recharts/y-axis {:scale :sqrt :domain [0 max]
                        :tickFormatter                      ;; u/int-fmt
                        (fn [n] (let [num (int (/ n 1000))]
                                  (if (zero? num)
                                    num
                                    (str (u/int-fmt num) "K"))))}]
      [recharts/tooltip {:formatter u/int-fmt}]
      [recharts/bar {:dataKey :expected
                     :name "예상값"
                     :type "monotone"
                     :fillOpacity 0.2
                     :stroke "#1890ff"
                     :fill "#1890ff"}]]]))

(def component
  (ui/constructor
   {:renderer conversion-funnel
    :subscription-deps [:optimize-stats
                        :ridgeline]}))
