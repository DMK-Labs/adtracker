(ns trendtracker.ui.pages.manage
  (:require [keechma.ui-component :as ui]
            [antizer.reagent :as ant]))

(defn render [ctx]
  [:div
   [:div.content-header
    [:h2 "Smart Ad Manager"]]
   [:div.content
    [ant/card {:title "광고그룹별 성과"}
     [(ui/component ctx :scatter-chart)
      [{:ctr 0.1 :impressions 200 :profit 100}
       {:ctr 0.2 :impressions 390 :profit 20}
       {:ctr 0.01 :impressions 990 :profit 30}
       {:ctr 0.02 :impressions 590 :profit 40}
       {:ctr 0.09 :impressions 190 :profit 90}]
      :ctr :impressions
      "클릭률" "노출수"]]]])

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:scatter-chart]}))
