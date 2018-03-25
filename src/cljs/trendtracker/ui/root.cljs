(ns trendtracker.ui.root
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [<cmd route> sub>]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  (let [current-page (:page (route> ctx))
        subpage      (:subpage (route> ctx))]
    (if (= "login" current-page)
      [(ui/component ctx :login-page)]

      [ant/layout {:style {:min-height "calc(100vh)"}}
       [(ui/component ctx :header)]
       [ant/layout
        [(ui/component ctx :sider)]
        [ant/layout
         [ant/layout-content
          (case current-page
            "settings"     [(ui/component ctx :settings-page)]
            "dashboard"    [(ui/component ctx :dashboard-page)]
            "optimize"     (if subpage
                             [(ui/component ctx :optimize-new-page)]
                             [(ui/component ctx :optimize-page)])
            "keyword-tool" [(ui/component ctx :keyword-tool-page)]
            "overview"     [(ui/component ctx :overview-page)]
            "manage"       [(ui/component ctx :manage-page)]
            [:div.content "404: page not found"])]
         [(ui/component ctx :footer)]]]])))

(def component
  (ui/constructor
   {:renderer       render
    :component-deps [:header
                     :sider
                     :footer
                     :login-page
                     :dashboard-page
                     :optimize-page
                     :optimize-new-page
                     :settings-page
                     :keyword-tool-page
                     :overview-page
                     :manage-page]}))
