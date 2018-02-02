(ns trendtracker.ui.components.header
  (:require [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub> <cmd route>]]
            [keechma.ui-component :as ui]
            [reagent.core :as r]
            [clojure.string :as string]
            [trendtracker.edb :refer [get-item-by-id]]))

(defn client-menu [ctx]
  (let [managed-clients (sub> ctx :managed-clients)
        current-client (sub> ctx :current-client)]
    [ant/menu {:onClick #(<cmd ctx :set (js/parseInt (.-key %)))
               :selectedKeys [(str (:customer_id current-client))]}
     (map (fn [client]
            [ant/menu-item {:key (:customer_id client)}
             [:a (:login_id client)]])
          managed-clients)]))

(defn user [ctx]
  (let [current-user (sub> ctx :current-user)
        current-client (sub> ctx :current-client)
        route (route> ctx)]
    [:span
     [ant/dropdown
      {:placement "bottomCenter"
       :overlay (r/as-element (client-menu ctx))}
      (if current-client
        [:a {:style {:color "rgba(0, 0, 0, .65)"}}
         (:login_id current-client) [ant/icon {:type "down" :style {:margin-left "4px"}}]]
        [ant/icon {:type "loading"}])]
     [ant/divider {:type "vertical"}]
     [ant/dropdown
      {:placement "bottomCenter"
       :overlay (r/as-element
                 [ant/menu
                  [ant/menu-item
                   [:a {:href (ui/url ctx (assoc route :page "user"))}
                    [:span [ant/icon {:type "setting"}] " 계정설정"]]]
                  [ant/menu-item
                   [:a {:href (ui/url ctx (assoc route :page "logout"))}
                    [:span [ant/icon {:type "logout"}] " 로그아웃"]]]])}
      [:a {:style {:margin-right "16px"}}
       (:name current-user)]]]))

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
     [user ctx]
     [notifications ctx]]]])

(def component
  (ui/constructor
   {:renderer render
    :topic :current-client
    :subscription-deps [:current-user
                        :managed-clients
                        :current-client]}))
