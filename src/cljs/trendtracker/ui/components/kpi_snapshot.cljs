(ns trendtracker.ui.components.kpi-snapshot
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]
            [trendtracker.ui.components.common :as common]
            [trendtracker.utils :as u]))

(defn snapshot
  [data prev-data title k color]
  (let [curr-sum (if (= :roas k)
                   (/ (u/sum :revenue data)
                      (u/sum :cost data))
                   (u/sum k data))
        prev-sum (if (= :roas k)
                   (/ (u/sum :revenue prev-data)
                      (u/sum :cost prev-data))
                   (u/sum k prev-data))]
    [ant/card {:title (common/title-w-info title)}
     [:div
      [:span
       [:span {:display "block"} "이전 동일 기간 대비"]
       [:br]
       [ant/row
        [:h2 {:style {:margin-bottom 0}}
         (if (= :roas k)
           (u/pct-fmt 2 curr-sum)
           (str "₩" (u/int-fmt curr-sum)))]
        [:span {:style {:display "inline"}}
         (common/delta-widget (u/delta prev-sum curr-sum) (= :cost k))]]]
      [recharts/responsive-container {:height 42}
       [recharts/composed-chart {:data data}
        [recharts/tooltip]
        [(if (> (count data) 14)        ; too may bar charts -> area
           recharts/area
           recharts/bar)
         {:type :monotone
          :dataKey k
          :dot nil
          :fillOpacity 0.11
          :stroke color
          :fill color}]]]]]))

(defn render [ctx title data-key color]
  (let [stats (ui/subscription ctx :stats)]
    [snapshot @stats @stats title data-key color]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:stats]}))
