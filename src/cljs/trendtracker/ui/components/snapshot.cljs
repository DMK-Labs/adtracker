(ns trendtracker.ui.components.snapshot
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]
            [trendtracker.ui.components.common :as common]
            [trendtracker.utils :as u]
            [reagent.core :as r]))

(def ^:private height 64)

(defn pure-draw [{:keys [title info sum-str delta data k color down-is-good?]}]
  [:div
   [:span
    (common/title-w-info title info)
    [ant/row {:type "flex" :align "bottom" :gutter 8}
     [ant/col [:h2 {:style {:margin-bottom 0}} sum-str]]
     [ant/col (common/delta-widget delta down-is-good?)]]]
   [recharts/responsive-container {:height height}
    [recharts/composed-chart {:data data}
     [recharts/x-axis {:mirror true :tick false :dataKey :during :padding {:left 0 :right 0}}]
     [recharts/tooltip {:formatter (if (= :roas k)
                                     (fn [x] (u/pct-fmt x))
                                     (comp u/krw int))}]
     [(if (> (count data) 10) recharts/area recharts/bar)
      {:type :monotone
       :dataKey k
       :dot nil
       :fillOpacity 0.11
       :stroke color
       :name title
       :fill color}]]]])

(defn budget-draw [{:keys [title info sum-str delta down-is-good?
                           sum budget]}]
  [:div
   [:span
    (common/title-w-info title info)
    [ant/row {:type "flex" :align "bottom" :gutter 8}
     [ant/col [:h2 {:style {:margin-bottom 0}} sum-str]]
     [ant/col (common/delta-widget delta down-is-good?)]]]
   [:div {:style {:height height}}
    [:div {:style {:padding-top 12}}
     "예산: " (u/krw budget)]
    [ant/progress {:percent (min 100 (int (* 100 (/ sum budget))))}]]])

(defmulti snapshot (fn [kpi _] kpi))


(defmethod snapshot :cost [kpi stats]
  (let [current (:curr stats)
        sum (u/sum kpi current)
        prev-sum (u/sum kpi (:prev stats))
        delta (u/delta prev-sum sum)
        color "#ff85c0"]
    (pure-draw {:title "소진 비용"
                :info "선택된 기간 동안 집행된 총 광고비"
                :sum-str (u/krw sum)
                :delta delta
                :data current
                :k kpi
                :color color
                :down-is-good? true})))

(defmethod snapshot :budget [kpi stats budget]
  (let [kpi :cost
        current (:curr stats)
        sum (u/sum kpi current)
        prev-sum (u/sum kpi (:prev stats))
        delta (u/delta prev-sum sum)
        color "#ff85c0"]
    (budget-draw {:title "소진 비용"
                  :info "선택된 기간 동안 집행된 총 광고비"
                  :sum-str (u/krw sum)
                  :delta delta
                  :down-is-good? true
                  :sum sum
                  :budget budget})))

(defmethod snapshot :revenue [kpi stats]
  (let [current (:curr stats)
        sum (u/sum kpi current)
        prev-sum (u/sum kpi (:prev stats))
        delta (u/delta prev-sum sum)
        color "#a0d911"]
    (pure-draw {:title "매출"
                :info "선택된 기간 동안 추적된 총 매출"
                :sum-str (u/krw sum)
                :delta delta
                :data current
                :k kpi
                :color color
                :down-is-good? false})))

(defmethod snapshot :profit [kpi stats]
  (let [current (:curr stats)
        sum (- (u/sum :revenue current)
               (u/sum :cost current))
        prev-sum (- (u/sum :revenue (:prev stats))
                    (u/sum :cost (:prev stats)))
        delta (u/delta prev-sum sum)
        color "#a0d911"]
    (pure-draw {:title "이윤"
                :info "선택된 기간 동안 추적된 총 이윤"
                :sum-str (u/krw sum)
                :delta delta
                :data (map #(assoc % :profit
                                     (- (:revenue %)
                                        (:cost %)))
                           current)
                :k kpi
                :color color
                :down-is-good? false})))

(defmethod snapshot :roas [kpi stats]
  (let [current (:curr stats)
        sum (/ (u/sum :revenue current)
               (u/sum :cost current))
        prev-sum (/ (u/sum :revenue (:prev stats))
                    (u/sum :cost (:prev stats)))
        delta (u/delta prev-sum sum)
        color "#9254de"]
    (pure-draw {:title "ROAS"
                :info "광고 수익률 (Return on Ad Spend)"
                :sum-str (u/pct-fmt sum)
                :delta delta
                :data current
                :k kpi
                :color color
                :down-is-good? false})))

(defmethod snapshot :cpc [kpi stats]
  (let [current (map #(assoc % :cpc (/ (:cost %)
                                       (:clicks %)))
                     (:curr stats))
        sum (/ (u/sum :cost current)
               (u/sum :clicks current))
        prev-sum (/ (u/sum :cost (:prev stats))
                    (u/sum :clicks (:prev stats)))
        delta (u/delta prev-sum sum)
        color "#fa541c"]
    (pure-draw {:title "CPC"
                :info "평균 클릭당 비용"
                :sum-str (u/krw (int sum))
                :delta delta
                :data current
                :k kpi
                :color color
                :down-is-good? true})))

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
        color "#52c41a"]
    (pure-draw {:title "CPM"
                :info "1,000개의 노출당 비용"
                :sum-str (u/krw (int (* 1000 sum)))
                :delta delta
                :data current
                :k kpi
                :color color
                :down-is-good? true})))

(defmethod snapshot :cpa [kpi stats]
  (let [current (let [v (->> stats
                             :curr
                             (map #(assoc % :cpa (if (zero? (:conversions %))
                                                   nil
                                                   (/ (:cost %) (:conversions %)))))
                             (map #(select-keys % [:cpa :during :cost :conversions])))]
                  v)
        sum (/ (u/sum :cost current)
               (u/sum :conversions current))
        prev-sum (/ (u/sum :cost (:prev stats))
                    (u/sum :conversions (:prev stats)))
        delta (u/delta prev-sum sum)
        color "#faad14"]
    (pure-draw {:title "CPA"
                :info "전환당 비용"
                :sum-str (u/krw (int sum))
                :delta delta
                :data current
                :k kpi
                :color color
                :down-is-good? true})))

(defmethod snapshot :default []
  [ant/card
   "No render function defined for this key"])

(defn render [ctx data-key]
  (let [stats (sub> ctx :daily-stats)
        loading? (= :pending (:status (sub> ctx :daily-stats-meta)))
        budget (:budget (sub> ctx :optimize-settings))]
    ;; TODO: turn this into a widget selectable by KPI (hence the ratom)
    [ant/spin {:spinning loading?}
     [ant/card
      [snapshot data-key stats budget]]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:daily-stats :daily-stats-meta
                        :optimize-settings]}))
