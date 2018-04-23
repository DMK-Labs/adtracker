(ns trendtracker.ui.components.sider
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [route> sub>]]
            [keechma.ui-component :as ui]
            [reagent.core :as r]))

(defn render [ctx]
  (let [kws (sub> ctx :keyword-tool)
        {:keys [page client]} (route> ctx)]
    [ant/layout-sider {:collapsible true
                       :breakpoint "lg"
                       :collapsed-width 64
                       ;; :trigger nil
                       :width 180}
     [ant/affix {:offset 56}
      [ant/menu {:theme :dark
                 :style {:border-right 0}
                 :mode "inline"
                 :selected-keys [page]
                 :on-click #(let [page (:key (js->clj % :keywordize-keys true))]
                              (ui/redirect
                               ctx
                               (merge {:page page
                                       :client client}
                                      (when (and (= "keyword-tool" page)
                                                 (:result kws))
                                        {:subpage "result"}))))}

       [ant/menu-item {:key "dashboard"}
        [ant/icon {:type "dashboard"}]
        [:span "대쉬보드"]]

       [ant/menu-item {:key "insights"}
        [ant/icon {:type "bulb"}]
        [:span "추천 사항"]]

       #_[ant/menu-item {:key "optimize"}
          [ant/icon {:type "rocket"}]
          [:span "최적화"]]

       [ant/menu-item {:key "keywords"}
        [ant/icon {:type "profile"}]
        [:span "키워드"]]

       [ant/menu-item {:key "creatives"}
        [ant/icon {:type "appstore-o"}]
        [:span "광고 소재"]]

       (when (= "settings" page)
         [ant/menu-item {:key "settings"}
          [ant/icon {:type "setting"}]
          [:span "설정"]])]]]))


(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:keyword-tool]}))
