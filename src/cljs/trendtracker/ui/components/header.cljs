(ns trendtracker.ui.components.header
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [reagent.core :as r]))

(defn user [ctx]
  [ant/popover
   {:placement "bottomRight"
    :arrowPointAtCenter true
    :content (r/as-element
              [:div
               [:a {:href (ui/url ctx {:page "user"})}
                [:div [ant/icon {:type "setting"}] " 계정설정"]]
               [:a {:href (ui/url ctx {:page "login"})}
                [:div [ant/icon {:type "logout"}] " 로그아웃"]]])}
   [:a {:style {:margin-right "16px"}}
    "dashcrab"
    [ant/icon {:type "down" :style {:margin-left "8px"}}]]])

(defn notifications [ctx]
  [ant/popover
   {:trigger "click"
    :placement "bottomRight"
    :arrowPointAtCenter true
    :title "알림사항"
    :content (r/as-element
              [ant/list
               {:dataSource [(str "최적화 실시: " (js/moment))
                             (str "최적화 실시: " (.subtract (js/moment) 1 "w"))]
                :renderItem (fn [item]
                              (r/as-element
                               [ant/list-item
                                [ant/list-item-meta
                                 {:description item}]]))}])}
   [ant/badge {:dot true} ;; FIXME: hook up to actual notifications
    [:a {:style {:color "inherit"}}
     [ant/icon {:type "bell" :style {:font-size 16}}]]]])

(defn render [ctx]
  [ant/affix {:style {:height "56px"}}
   [ant/layout-header
    [:a {:href "http://trendtracker.co.kr"}
     [:img {:src "/img/logo/tt-logo.png"
            :style {:height 34 :width 152}}]]
    [:div {:style {:float "right"}}
     [:span "NineBridge, Inc."]
     [ant/divider {:type "vertical"}]
     [user ctx]
     [notifications ctx]]]])

(def component
  (ui/constructor
   {:renderer render}))
