(ns trendtracker.ui.components.snapshot
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]
            [trendtracker.ui.components.common :as common]
            [trendtracker.utils :as u]))

(defn pure-draw [title info sum-str delta data k color down-is-good?]
  [ant/card {:title (common/title-w-info title info)}
   [:div
    [:span
     ;; [:span {:display "block"} "이전 기간 대비"]
     [ant/row {:type "flex" :align "bottom" :gutter 8}
      [ant/col [:h2 {:style {:margin-bottom 0}} sum-str]]
      [ant/col (common/delta-widget delta down-is-good?)]]]
    [recharts/responsive-container {:height 42}
     [recharts/composed-chart {:data data}
      [recharts/tooltip]
      [(if (> (count data) 12) recharts/area recharts/bar)
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
    (pure-draw "비용" "선택된 기간 동안 집행된 총 광고비"
               (u/krw sum) delta current kpi color true)))

(defmethod snapshot :revenue [kpi stats]
  (let [current (:curr stats)
        sum (u/sum kpi current)
        prev-sum (u/sum kpi (:prev stats))
        delta (u/delta prev-sum sum)
        color "#52c41a"]
    (pure-draw "매출" "선택된 기간 동안 추적된 총 매출"
               (u/krw sum) delta current kpi color false)))

(defmethod snapshot :roas [kpi stats]
  (let [current (:curr stats)
        sum (/ (u/sum :revenue current)
               (u/sum :cost current))
        prev-sum (/ (u/sum :revenue (:prev stats))
                    (u/sum :cost (:prev stats)))
        delta (u/delta prev-sum sum)
        color "#B5A1DE"]
    (pure-draw "ROAS" "광고 수익률 (Return on Ad Spend)"
               (u/pct-fmt sum) delta current kpi color false)))

(defmethod snapshot :cpc [kpi stats]
  (let [current (map #(assoc % :cpc (/ (:cost %)
                                       (:clicks %)))
                     (:curr stats))
        sum (/ (u/sum :cost current)
               (u/sum :clicks current))
        prev-sum (/ (u/sum :cost (:prev stats))
                    (u/sum :clicks (:prev stats)))
        delta (u/delta prev-sum sum)
        color "#aaa"]
    (pure-draw "CPC" "클릭당 비용"
               (u/krw (int sum)) delta current kpi color true)))

(defmethod snapshot :cpm [kpi stats]
  (let [current (map #(assoc % :cpm (/ (:cost %)
                                       (/ (:impressions %)
                                          1000)))
                     (:curr stats))
        sum (/ (u/sum :cost current)
               (u/sum :impressions current))
        prev-sum (/ (u/sum :cost (:prev stats))
                    (u/sum :impressions (:prev stats)))
        delta (u/delta prev-sum sum)
        color "#aaa"]
    (pure-draw "CPM" "1,000개의 노출당 비용"
               (u/krw (int (* 1000 sum))) delta current kpi color true)))

(defmethod snapshot :cpa [kpi stats]
  (let [current (map #(assoc % :cpa (/ (:cost %)
                                       (:conversions %)))
                     (:curr stats))
        sum (/ (u/sum :cost current)
               (u/sum :conversions current))
        prev-sum (/ (u/sum :cost (:prev stats))
                    (u/sum :conversions (:prev stats)))
        delta (u/delta prev-sum sum)
        color "#aaa"]
    (pure-draw "CPA" "전환당 비용"
               (u/krw (int sum)) delta current kpi color true)))

(defmethod snapshot :default []
  [ant/card
   "No render function defined for this key"])

(defn render [ctx data-key]
  (let [stats (sub> ctx :daily-stats)]
    [ant/spin {:spinning (empty? stats)}
     [snapshot data-key stats]]))

(def component
  (ui/constructor
    {:renderer render
    :subscription-deps [:daily-stats]}))
