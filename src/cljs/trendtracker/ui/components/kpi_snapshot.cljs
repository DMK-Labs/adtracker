(ns trendtracker.ui.components.kpi-snapshot
  (:require [keechma.ui-component :as ui]
            [reagent.core :as r]))

(defn snapshot
  "`type` is :krw or :pct"
  [data prev-data k title color]
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
        [:h3 {:style {:margin-bottom 0}}
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
         {:type :monotone :dataKey k :dot nil :fillOpacity 0.11 :stroke color :fill color}]
        ;; [recharts/area {:type :monotone :dataKey :cost
        ;;                 :stroke "rgba(0, 0, 0, 0.25)" :fill "#dfdfdf"}]
        ]]]]))

(defn render [ctx]
  )

(def component (ui/constructor {:renderer render}))
