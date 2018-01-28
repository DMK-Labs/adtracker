(ns trendtracker.ui.pages.user
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> <cmd]]
            [reagent.core :as r]
            [antizer.reagent :as ant]))

(defn select [ctx]
  (let [current-client (sub> ctx :current-client)
        managed-clients (sub> ctx :managed-clients)]
    [ant/select
     {:value (:customer_id current-client)
      :onChange #(<cmd ctx :set %)
      :style {:min-width 200}}
     (map (fn [client]
            [ant/select-option {:value (:customer_id client)
                                :key (:customer_id client)}
             (:company_name client)])
          managed-clients)]))

(defn render [ctx]
  (let [{:keys [email name tenant naver-id]} (sub> ctx :current-user)
        managed-clients (sub> ctx :managed-clients)]
    [:div.content
     [ant/row {:gutter 16}
      [ant/col {:xl 8}
       [ant/card
        {:title "사용자 정보"}
        [:ul {:style {:line-height "2.5"}}
         [:li "사용자 ID: " name]
         [:li "회사명: " tenant]
         [:li "Email: " email]
         [:li "Naver ID: " naver-id]
         [:li "현재 관리중 계정: "
          [select ctx]]]]]
      [ant/col {:xl 16}
       [ant/card {:title "대행 관리 계정"}
        [ant/spin {:spinning (empty? managed-clients)}
         [:p [:a {:href "https://manage.searchad.naver.com/customers/me/myinfo/customer-links"
                  :target "_blank"}
              "Naver에서 권한 관리 " [ant/icon {:type "edit"}]]]
         [ant/table
          {:dataSource managed-clients
           :pagination false
           :rowKey :customer_id
           :columns [{:title "네이버 광고 ID" :dataIndex :customer_id}
                     {:title "로그인 ID" :dataIndex :login_id}
                     {:title "회사명" :dataIndex :company_name}
                     {:title "권한 부여일" :dataIndex :reg_at}]}]]]]]]))

(def component
  (ui/constructor
   {:renderer render
    :topic :current-client
    :subscription-deps [:current-user
                        :managed-clients
                        :current-client]}))
