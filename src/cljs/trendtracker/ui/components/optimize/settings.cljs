(ns trendtracker.ui.components.optimize.settings
  (:require [trendtracker.utils :as u]
            [keechma.ui-component :as ui]
            [antizer.reagent :as ant]
            [keechma.toolbox.ui :refer [sub> route>]]
            [reagent.core :as r]))

(defn new-opt-btn [ctx]
  [:div
   [:a {:href (ui/url ctx (assoc (route> ctx)
                                 :page "optimize"
                                 :subpage "settings"))}
    [ant/button {:icon "setting" :style {:margin-right 8}} "설정 변경"]]])

(defn opt-settings [ctx]
  (let [settings (sub> ctx :optimize-settings)
        {:keys [budget objective bid-limit]} settings]
    (if (= "no-settings" settings)
      [ant/card {:title "최적화 설정"}
       [ant/alert {:message "아직 최적화를 설정하지 않으셨습니다."
                   :type "warning"
                   :showIcon true
                   :style {:margin-bottom 16}}]
       [new-opt-btn ctx]]
      [ant/card {:title "최적화 설정"
                 :extra (r/as-element
                         [new-opt-btn ctx])}
       [:ul.opt-settings
        [:li.opt-setting {:style {:border-right "1px solid #e6ebf1"}}
         [:div {:style {:margin-bottom 8}}
          [:span.summary-title (u/krw budget)]
          [ant/icon {:type "edit"}]]
         [:p "월 예산"]]
        [:li.opt-setting {:style {:border-right "1px solid #e6ebf1"}}
         [:h2 {:style {:text-transform "capitalize"}}
          objective]
         [:p "목표 지표"]]
        [:li.opt-setting
         [:h2 (u/krw bid-limit)]
         [:p "입찰 Limit"]]]])))

(def component
  (ui/constructor
   {:renderer opt-settings
    :subscription-deps [:optimize-settings]}))

