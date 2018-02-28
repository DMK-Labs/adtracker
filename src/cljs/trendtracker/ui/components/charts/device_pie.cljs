(ns trendtracker.ui.components.charts.device-pie
  (:require [reacharts.recharts :as recharts]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub>]]
            [trendtracker.utils :as u]
            [antizer.reagent :as ant]))

(def colors ["#597ef7" "#36cfc9"])

;; [ant/select {:defaultValue :clicks
;;              :style {:margin-top 16}}
;;  [ant/select-option {:value :clicks} "클릭수"]
;;  [ant/select-option {:value :impressions} "노출수"]
;;  [ant/select-option {:value :cost} "비용"]]

(defn render [ctx]
  [ant/card {:title "PC/Mobile 비교"}
   [ant/row
    [ant/col {:span 16}

     [ant/table
      {:style {:margin-top 16}
       :dataSource [{:name "PC"
                     :clicks 1291
                     :cost 39129
                     :impressions 3333
                     :conversions 100
                     :revenue 89129}
                    {:name "Mobile"
                     :clicks 19299
                     :cost 39129
                     :impressions 3333
                     :conversions 100
                     :revenue 89129}]
       :columns [{:title "구분" :dataIndex :name}
                 {:title "비용" :dataIndex :cost}
                 {:title "노출" :dataIndex :impressions}
                 {:title "클릭" :dataIndex :clicks}
                 {:title "전환" :dataIndex :conversions}
                 {:title "매출" :dataIndex :revenue}]
       :pagination false
       :size :medium
       :bordered true}]]

    [ant/col {:span 8}
     [recharts/responsive-container {:height 200}
      [recharts/pie-chart
       [recharts/tooltip]
       [recharts/pie {:data [{:name "PC"
                              :value 80
                              :fill (first colors)
                              :fillOpacity 0.2
                              :stroke (first colors)}
                             {:name "Mobile"
                              :value 20
                              :fill (second colors)
                              :fillOpacity 0.2
                              :stroke (second colors)}]
                      :dataKey :value
                      :label {:fillOpacity 1}
                      :innerRadius 35
                      :outerRadius 80}]]]]]])

(def component
  (ui/constructor
   {:renderer render}))
