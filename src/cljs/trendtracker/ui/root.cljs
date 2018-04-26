(ns trendtracker.ui.root
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [<cmd route> sub>]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  (let [current-page (:page (route> ctx))
        subpage (:subpage (route> ctx))]
    (if (= "login" current-page)
      [(ui/component ctx :login-page)]

      [ant/layout {:style {:min-height "calc(100vh)"}}
       [(ui/component ctx :header)]
       [ant/layout
        [(ui/component ctx :sider)]
        [ant/layout
         [ant/layout-content
          (case current-page
            "settings" [(ui/component ctx :settings-page)]
            "dashboard" [(ui/component ctx :dashboard-page)]
            "overview" [(ui/component ctx :overview-page)]
            "insights" [(ui/component ctx :insights-page)]
            "adgroups" [(ui/component ctx :adgroups-page)]
            "keywords" [(ui/component ctx :keywords)]
            "creatives" [(ui/component ctx :creatives)]
            "optimize" (if-not subpage
                         [(ui/component ctx :optimize-page)]
                         [(ui/component ctx :optimize-new-page)])
            [:div.content "404: page not found"])]
         [(ui/component ctx :footer)]]]])))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:header
                     :sider
                     :footer
                     :login-page
                     :dashboard-page
                     :optimize-page
                     :optimize-new-page
                     :settings-page
                     :overview-page

                     :adgroups-page

                     ;;Insights
                     :insights-page
                     :creatives
                     :keywords]}))
