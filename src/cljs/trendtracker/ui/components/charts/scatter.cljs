(ns trendtracker.ui.components.charts.scatter
  (:require [reacharts.recharts :as recharts]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [trendtracker.utils :as u]
            [antizer.reagent :as ant]))

(defn scatter-chart [ctx]
  (let [data (sub> ctx :adgroups)
        {:keys [x y z x-label y-label z-label]} {:x :ctr
                                                 :y :impressions
                                                 :z :profit
                                                 :x-label "클릭률"
                                                 :y-label "노출수"
                                                 :z-label "이윤"}]
    [ant/spin
     {:spinning (= :pending (:status (sub> ctx :adgroups-meta)))}
     [recharts/responsive-container {:height 220}
      (let [clicks (u/sum :clicks data)
            impressions (u/sum :impressions data)
            avg-ctr (/ clicks impressions)
            avg-imp (/ impressions (count data))]
        [recharts/scatter-chart
         [recharts/tooltip {:formatter u/int-fmt}]
         [recharts/x-axis {:dataKey x
                           :scale :sqrt
                           :tickFormatter #(u/pct-fmt %)
                           :type "number"
                           :padding {:left 25 :right 25}}
          [recharts/label {:value x-label :position "insideBottom" :offset 0}]]
         [recharts/y-axis {:dataKey y
                           :scale :sqrt
                           :tickFormatter #(str (u/int-fmt (/ % 1000)) "K")
                           :padding {:top 25 :bottom 25}}
          [recharts/label {:value y-label :position "insideLeft" :offset 0 :angle -90}]]
         [recharts/z-axis {:dataKey z :range [20 800] :value z-label}]
         [recharts/scatter
          {:isAnimationActive false
           :data data
           :stroke "#fa8c16" :fill "#ffe7ba"}]
         [recharts/scatter
          {:isAnimationActive false
           :data (filter #(> avg-imp (:impressions %)) data)
           :stroke "#bfbfbf" :fill "#f5f5f5"}]
         [recharts/scatter
          {:isAnimationActive false
           :data (filter #(< avg-ctr (:ctr %)) data)
           :stroke "#52c41a" :fill "#d9f7be"}]
         [recharts/scatter
          {:isAnimationActive false
           :data (filter #(and (< avg-ctr (:ctr %))
                               (< avg-imp (:impressions %)))
                         data)
           :stroke "#1890ff" :fill "#bae7ff"}]
         [recharts/reference-line {:x avg-ctr
                                   :stroke "red"
                                   :label "평균 CTR"}]
         [recharts/reference-line {:y avg-imp
                                   :stroke "green"
                                   :label "평균 노출량"}]])]]))

(def component
  (ui/constructor
   {:renderer scatter-chart
    :subscription-deps [:adgroups :adgroups-meta]}))
