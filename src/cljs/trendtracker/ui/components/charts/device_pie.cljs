(ns trendtracker.ui.components.charts.device-pie
  (:require [reacharts.recharts :as recharts]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub>]]
            [trendtracker.utils :as u]
            [antizer.reagent :as ant]
            [reagent.core :as r]
            [clojure.string :as string]))

(def colors ["#faad14" "#36cfc9"])

;; [ant/select {:defaultValue :clicks
;;              :style {:margin-top 16}}
;;  [ant/select-option {:value :clicks} "클릭수"]
;;  [ant/select-option {:value :impressions} "노출수"]
;;  [ant/select-option {:value :cost} "비용"]]

(defonce selected (r/atom :clicks))

(defn selector [key]
  (fn [_]
    (clj->js
     {:onClick #(reset! selected key)})))

(def columns [{:title     "기기"
               :dataIndex :pc_mobile_type
               :fixed     "left"}
              {:title        "비용"
               :dataIndex    :cost
               :className    "numbers"
               :render       u/krw
               :sorter       (u/sorter-by :cost)
               :onCell       (selector :cost)
               :onHeaderCell (selector :cost)}
              {:title        "노출"
               :dataIndex    :impressions
               :className    "numbers"
               :render       u/int-fmt
               :sorter       (u/sorter-by :impressions)
               :onCell       (selector :impressions)
               :onHeaderCell (selector :impressions)}
              {:title        "클릭"
               :dataIndex    :clicks
               :className    "numbers"
               :render       u/int-fmt
               :sorter       (u/sorter-by :clicks)
               :onCell       (selector :clicks)
               :onHeaderCell (selector :clicks)}
              {:title     "클릭률"
               :dataIndex :ctr
               :className "numbers"
               :render    #(u/pct-fmt %)
               :sorter    (u/sorter-by :ctr)}
              ;; :onCell (selector :clicks)
              ;; :onHeaderCell (selector :clicks)

              {:title        "전환"
               :dataIndex    :conversions
               :className    "numbers"
               :render       u/int-fmt
               :sorter       (u/sorter-by :conversions)
               :onCell       (selector :conversions)
               :onHeaderCell (selector :conversions)}
              {:title     "전환률"
               :dataIndex :cvr
               :className "numbers"
               :render    #(u/pct-fmt %)
               :sorter    (u/sorter-by :cvr)}
              ;; :onCell (selector :clicks)
              ;; :onHeaderCell (selector :clicks)

              {:title        "매출"
               :dataIndex    :revenue
               :className    "numbers"
               :render       u/krw
               :sorter       (u/sorter-by :revenue)
               :onCell       (selector :revenue)
               :onHeaderCell (selector :revenue)}])

(def radian (/ js/Math.PI 180))

(defn- labeller [args]
  (let [{:keys [cx cy outerRadius value name midAngle] :as m}
        (js->clj args :keywordize-keys true)
        radius (* 0.2 outerRadius)
        x      (+ cx (* radius (js/Math.cos (* (- midAngle) radian))))
        y      (+ cy (* radius (js/Math.sin (* (- midAngle) radian))))]
    (r/as-element
     [:text {:x x :y y :textAnchor (if (> x cx) "start" "end") :dominantBaseline "central"}
      (str (first name) ": " (u/pct-fmt value))])))

(defn render [ctx]
  (let [stats (sub> ctx :pc-mobile-split)
        stats-meta (sub> ctx :pc-mobile-split-meta)
        [a b] (map @selected stats)
        r1    (/ a (+ a b))
        r2    (/ b (+ a b))]
    [ant/card {:title (str "PC/Mobile 비교")
               :loading (= :pending (:status stats-meta))}
     [ant/row {:type "flex"}
      [ant/col {:span 17}
       [ant/table
        {:style      {:margin-top 16}
         :dataSource stats
         :rowKey     :pc_mobile_type
         :columns    columns
         :pagination false
         :scroll     {:x 1000}}]]
      [ant/col {:span 7}
       [:div {:style {:display "flex" :justify-content "space-around"}}
        [:div {:style {:width 161 :margin-left 16}}
         [:h3 (string/capitalize (name @selected))]
         [recharts/responsive-container {:height 161}
          [recharts/pie-chart
           [recharts/tooltip {:formatter #(u/pct-fmt %)}]
           [recharts/pie
            {:data              [{:name        (:pc_mobile_type (first stats))
                                  :value       r1
                                  :fill        (first colors)
                                  :fillOpacity 0.2
                                  :stroke      (first colors)}
                                 {:name        (:pc_mobile_type (second stats))
                                  :value       r2
                                  :fill        (second colors)
                                  :fillOpacity 0.2
                                  :stroke      (second colors)}]
             :dataKey           :value
             :labelLine         false
             :label             labeller
             :outerRadius       80
             :animationBegin    200
             :animationDuration 750}]]]]]]]]))

(def component
  (ui/constructor
   {:renderer          render
    :subscription-deps [:pc-mobile-split :pc-mobile-split-meta]}))
