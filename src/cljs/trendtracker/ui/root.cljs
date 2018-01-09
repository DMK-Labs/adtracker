(ns trendtracker.ui.root
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [<cmd route> sub>]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  (let [current-route (route> ctx)
        current-page  (:page current-route)]
    (if (= "login" current-page)
      [(ui/component ctx :login-page)]
      [ant/layout
       {:style {:min-height "calc(100vh)"}}
       [(ui/component ctx :header)]
       [ant/layout
        [(ui/component ctx :sider)]
        [ant/layout
         [ant/layout-content
          (case current-page
            "user"         [(ui/component ctx :user-page)]
            "dashboard"    [(ui/component ctx :dashboard-page)]
            "optimize"     [(ui/component ctx :optimize-page)]
            "keyword-tool" [(ui/component ctx :keyword-tool-page)]
            "counter"      [(ui/component ctx :counter)]
            [:div.content "404: page not found"])]
         [(ui/component ctx :footer)]]]])))

(def component
  (ui/constructor
   {:renderer       render
    :component-deps [:header
                     :sider
                     :footer
                     :counter
                     :login-page
                     :dashboard-page
                     :optimize-page
                     :user-page
                     :keyword-tool-page]}))
