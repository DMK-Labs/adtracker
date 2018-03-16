(ns trendtracker.ui.components.charts.tabbed-charts
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]
            [reagent.core :as r]
            [trendtracker.ui.components.common :as common]
            [trendtracker.utils :as u]))

(defn title
  [title int-or-pct delta]
  (r/as-element
   [:div
    [:h3 {:style {:margin-bottom 0 :color "#666"}} title]
    [:h2 {:style {:margin-bottom 0}}
     (if (integer? int-or-pct)
       (u/int-fmt int-or-pct)
       (u/pct-fmt int-or-pct))]
    (common/delta-widget delta)]))

(defn chart [data data-key nm]
  (let [line-opts {:type "monotone" :stroke "#1890ff" :fill "#1890ff" :fillOpacity 0.11 :dot nil}
        y-formatter (let [pct (fn [x] (u/pct-fmt x))]
                      (if (#{:ctr :cvr :i2c} data-key)
                        pct
                        u/int-fmt))]
    [recharts/responsive-container {:height 211}
     [recharts/composed-chart {:data data}
      [recharts/cartesian-grid {:strokeDasharray "2 4"
                                :vertical false}]
      [recharts/x-axis {:dataKey :during
                        :minTickGap 15}]
      [recharts/y-axis {:tickFormatter y-formatter}]
      [recharts/line (merge line-opts {:dataKey (keyword (str "prev-" (name data-key)))
                                       :name (str "이전 기간 " nm)
                                       :strokeDasharray "4 6"})]
      [(if (< 14 (count data))
         recharts/line
         recharts/bar)
       (merge line-opts {:dataKey data-key :name nm})]
      [recharts/tooltip {:formatter y-formatter}]]]))

(defn tabbed-charts [ctx]
  (let [stats (sub> ctx :daily-stats)
        data (:curr stats)
        prev-data (:prev stats)

        impressions (u/sum :impressions data)
        clicks (u/sum :clicks data)
        conversions (u/sum :conversions data)
        ;; ctr (/ clicks impressions)
        ;; cvr (/ conversions clicks)
        ;; i2c (/ conversions impressions)

        pimpressions (u/sum :impressions prev-data)
        pclicks (u/sum :clicks prev-data)
        pconversions (u/sum :conversions prev-data)
        ;; pctr (/ pclicks pimpressions)
        ;; pcvr (/ pconversions pclicks)
        ;; pi2c (/ pconversions pimpressions)

        joined (map merge
                    data
                    (map #(u/prefix-keys % "prev-") prev-data))]
    [ant/spin {:spinning (empty? stats)}
     [ant/card
      (into [ant/tabs
             [ant/tabs-tab-pane
              {:key "1" :tab (title "노출수" impressions (u/delta pimpressions impressions))}
              [chart joined :impressions "노출수"]]
             [ant/tabs-tab-pane
              {:key "3" :tab (title "클릭수" clicks (u/delta pclicks clicks))}
              [chart joined :clicks "클릭수"]]
             ;[ant/tabs-tab-pane
             ; {:key "2" :tab (title "클릭률 (CTR)" ctr (u/delta pctr ctr))}
             ; [chart joined :ctr "클릭률"]]
             ]
            (when (or (some (comp pos? :conversions) data)
                      (some (comp pos? :conversions) prev-data))
              [[ant/tabs-tab-pane
                {:key "5" :tab (title "전환수" conversions (u/delta pconversions conversions))}
                [chart joined :conversions "전환수"]]
               ;[ant/tabs-tab-pane
               ; {:key "4" :tab (title "전환률 (CVR)" cvr (u/delta pcvr cvr))}
               ; [chart joined :cvr "전환률"]]
               ;[ant/tabs-tab-pane
               ; {:key "6" :tab (title "총 전환률 (I2C)" i2c (u/delta pi2c i2c))}
               ; [chart joined :i2c "총 전환률"]]
               ]))]]))

(def component
  (ui/constructor
   {:renderer tabbed-charts
    :subscription-deps [:daily-stats]}))
