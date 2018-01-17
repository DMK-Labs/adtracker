(ns trendtracker.ui.components.sider
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [route>]]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [ant/layout-sider {:collapsible true
                     :breakpoint "lg"
                     :collapsed-width 64}
   [ant/affix {:offset 56}
    [ant/menu {:theme :dark
               :style {:border-right 0}
               :mode "inline"
               :selected-keys [(:page (route> ctx))]
               :on-click #(ui/redirect
                           ctx
                           {:page (:key (js->clj % :keywordize-keys true))})}

     [ant/menu-item {:key "dashboard"}
      [ant/icon {:type "dashboard"}]
      [:span "대쉬보드"]]

     [ant/menu-item {:key "optimize"}
      [ant/icon {:type "rocket"}]
      [:span "입찰 최적화"]]

     [ant/menu-item {:key "keyword-tool"}
      [ant/icon {:type "tool"}]
      [:span "키워드 도구"]]

     [ant/menu-item {:key "manage" :disabled true}
      [ant/icon {:type "profile"}]
      [:span "광고 관리"]]]]])

(def component (ui/constructor {:renderer render}))
