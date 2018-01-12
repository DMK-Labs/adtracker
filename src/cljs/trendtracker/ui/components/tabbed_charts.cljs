(ns trendtracker.ui.components.tabbed-charts
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub>]]
            [keechma.ui-component :as ui]
            [reacharts.recharts :as recharts]
            [reagent.core :as r]
            [trendtracker.ui.components.common :as common]
            [trendtracker.utils :as u]))

(defn tab-title
  [title int-or-pct delta]
  (r/as-element
   [:div
    [:h3 {:style {:margin-bottom 0 :color "#666"}} title]
    [:h1 {:style {:margin-bottom 0}}
     (if (integer? int-or-pct)
       (u/int-fmt int-or-pct)
       (u/pct-fmt 2 int-or-pct))]
    (common/delta-widget delta)]))

(defn chart [data data-key]
  (let [line-opts {:type "monotone" :stroke "#1890ff" :dot nil}]
    [recharts/responsive-container {:height 300}
     [recharts/composed-chart {:data data}
      [recharts/legend {:vertical-align "bottom"}]
      [recharts/x-axis {:dataKey :during}]
      [recharts/y-axis]
      [recharts/line (merge line-opts {:dataKey data-key})]
      [recharts/line (merge line-opts {:dataKey (keyword (str "prev-" (name data-key)))
                                       :strokeDasharray "3 7"})]
      [recharts/tooltip]]]))

(defn tabbed-charts [ctx]
  (let [stats (sub> ctx :stats)
        data (:curr stats)
        prev-data (:prev stats)

        impressions (u/sum :impressions data)
        clicks (u/sum :clicks data)
        conversions (u/sum :conversions data)
        ctr (/ clicks impressions)
        cvr (/ conversions clicks)

        pimpressions (u/sum :impressions prev-data)
        pclicks (u/sum :clicks prev-data)
        pconversions (u/sum :conversions prev-data)
        pctr (/ pclicks pimpressions)
        pcvr (/ pconversions pclicks)

        joined (map merge
                    data
                    (map #(u/prefix-keys % "prev-") prev-data))]
    [ant/card
     [ant/tabs
      [ant/tabs-tab-pane
       {:key "1" :tab (tab-title "노출수" impressions (u/delta pimpressions impressions))}
       [chart joined :impressions]]
      [ant/tabs-tab-pane
       {:key "2" :tab (tab-title "클릭률 (CTR)" ctr (u/delta pctr ctr))}
       [chart joined :ctr]]
      [ant/tabs-tab-pane
       {:key "3" :tab (tab-title "클릭수" clicks (u/delta pclicks clicks))}
       [chart joined :clicks]]
      [ant/tabs-tab-pane
       {:key "4" :tab (tab-title "전환률 (CVR)" cvr (u/delta pcvr cvr))}
       [chart joined :cvr]]
      [ant/tabs-tab-pane
       {:key "5" :tab (tab-title "전환수" conversions (u/delta pconversions conversions))}
       [chart joined :conversions]]]]))

(def component
  (ui/constructor
   {:renderer tabbed-charts
    :subscription-deps [:stats]}))
