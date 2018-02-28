(ns trendtracker.ui.components.sider
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [route> sub>]]
            [keechma.ui-component :as ui]
            [reagent.core :as r]))

(defn render [ctx]
  (let [kws (sub> ctx :keyword-tool)
        route (route> ctx)
        current-page (:page route)
        client (:client route)]
    [ant/layout-sider {:collapsible true
                       :breakpoint "xl"
                       :collapsed-width 64}
     [ant/affix {:offset 56}
      [ant/menu {:theme :dark
                 :style {:border-right 0}
                 :mode "inline"
                 :selected-keys [current-page]
                 :on-click #(let [page (:key (js->clj % :keywordize-keys true))]
                              (ui/redirect
                               ctx
                               (merge {:page page
                                       :client client}
                                      (when (and (= "keyword-tool" page)
                                                 (:result kws))
                                        {:subpage "result"}))))
                 :defaultOpenKeys [:ad]}

       [ant/menu-sub-menu {:key :ad
                           :title (r/as-element
                                   [:span
                                    [ant/icon {:type "line-chart"}]
                                    [:span "Ad Tracker"]])}

        [ant/menu-item {:key "dashboard"}
         [ant/icon {:type "dashboard"}]
         [:span "대쉬보드"]]

        [ant/menu-item {:key "optimize"}
         [ant/icon {:type "rocket"}]
         [:span "입찰 최적화"]]

        [ant/menu-item {:key "keyword-tool"}
         [ant/icon {:type "tool"}]
         [:span "키워드 조사 도구"]]

        [ant/menu-item {:key "keywords"}
         [ant/icon {:type "profile"}]
         [:span "키워드 목록"]]

        [ant/menu-item {:key "manage"}
         [ant/icon {:type "bulb"}]
         [:span "광고 개선"]]

        (when (= current-page "user")
          [ant/menu-item {:key "user"}
               [ant/icon {:type "setting"}]
               [:span "계정 설정"]])]]]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:keyword-tool]}))
