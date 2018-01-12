(ns trendtracker.ui.components.snapshot
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]
            [trendtracker.ui.components.common :as common]
            [trendtracker.utils :as u]))

(defn pure-draw [title sum-str delta data k color down-is-good?]
  [ant/card {:title (common/title-w-info title)}
   [:div
    [:span
     [:span {:display "block"} "이전 기간 대비"]
     [ant/row
      [:h2 {:style {:margin-bottom 0}} sum-str]
      [:span {:style {:display "inline"}}
       (common/delta-widget delta down-is-good?)]]]
    [recharts/responsive-container {:height 42}
     [recharts/composed-chart {:data data}
      [recharts/tooltip]
      [(if (> (count data) 14) recharts/area recharts/bar)
       {:type :monotone
        :dataKey k
        :dot nil
        :fillOpacity 0.11
        :stroke color
        :fill color}]]]]])

(defmulti snapshot (fn [kpi _] kpi))

(defmethod snapshot :cost [kpi stats]
  (let [current (:curr stats)
        sum (u/sum kpi current)
        prev-sum (u/sum kpi (:prev stats))
        delta (u/delta prev-sum sum)
        color "#fa541c"]
    (pure-draw "비용" (u/int-fmt sum) delta current kpi color false)))

(defmethod snapshot :revenue [kpi stats]
  (let [current (:curr stats)
        sum (u/sum kpi current)
        prev-sum (u/sum kpi (:prev stats))
        delta (u/delta prev-sum sum)
        color "#52c41a"]
    (pure-draw "매출" (u/int-fmt sum) delta current kpi color false)))

(defmethod snapshot :roas [kpi stats]
  (let [current (:curr stats)
        sum (/ (u/sum :revenue current)
               (u/sum :cost current))
        prev-sum (/ (u/sum :revenue (:prev stats))
                    (u/sum :cost (:prev stats)))
        delta (u/delta prev-sum sum)
        color "#B5A1DE"]
    (pure-draw "ROAS" (u/pct-fmt sum) delta current kpi color false)))

(defmethod snapshot :default [kpi title stats]
  [:div "default"])

(defn render [ctx data-key]
  (let [stats (sub> ctx :stats)]
    [snapshot data-key stats]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:stats]}))
