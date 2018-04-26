(ns trendtracker.ui.pages.adgroups
  (:require [antizer.reagent :as ant]
   [keechma.ui-component :as ui]
   [keechma.toolbox.ui :refer [sub> route>]]
   [reagent.core :as r]
   [trendtracker.ui.components.common :as common]
   [trendtracker.ui.components.pure.form-inputs :refer [controlled-input]]
   [keechma.toolbox.forms.ui :as forms-ui]
   [trendtracker.helpers.download :as download]))


(defn keyword-search [ctx]
  (let [form-props [:adgroup-search :form]
        form-state (forms-ui/form-state> ctx form-props)]
    [ant/form {:on-submit #(forms-ui/<submit ctx form-props %)}
     [controlled-input ctx form-props :adgroup-search
      {:form-state form-state
       :placeholder "광고그룹 검색"
       :item-opts {:style {:margin-bottom 0}}
       :input-opts {:prefix (r/as-element [ant/icon {:type "search"}])}}]]))

(defn render [ctx]
  (let [breadcrumbs (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)
        adgroup-performances (ui/component ctx :adgroup-performances)
        scatter-chart (ui/component ctx :scatter-chart)

        route (route> ctx)
        adgroups (sub> ctx :adgroups)
        adgroups-meta (sub> ctx :adgroups-meta)]
    [:div
     [common/content-header
      [breadcrumbs]
      [ant/row {:type "flex" :justify "space-between"}
       [ant/col {:md 7 :xs 24}
        [:h2.page-title "광고그룹"]]
       [ant/col [date-range-picker]]]]

     [:div.content
      #_[ant/row {:gutter 16}
         [ant/col [ant/card [scatter-chart]]]]

      [ant/row {:gutter 16}
       [ant/col
        [ant/card
         [ant/row {:type "flex" :justify "end" :gutter 8 :align "middle"
                   :style {:margin-bottom 8}}
          [ant/col
           [keyword-search ctx]]
          [ant/col
           [ant/button {:icon "download"
                        :loading (= :pending (:status adgroups-meta))
                        :on-click #(download/download-csv
                                    {:filename "adgroups.csv"
                                     :header []
                                     :content adgroups})}]]]
         [:div
          (when (:aq route)
            [:div {:style {:margin-bottom 16}}
             [ant/divider {:style {:margin "16px 0"}}]
             [:div [ant/icon {:type "filter" :style {:margin-right 4}}] "검색 필터: "
              [ant/tag
               {:closable true :color "blue" :onClose #(ui/redirect ctx (dissoc route :aq))
                :onClick #(ui/redirect ctx (dissoc route :aq))}
               (:aq route)]]])
          [ant/row
           [ant/col
            [adgroup-performances]]]]]]]]]))

(def component
  (ui/constructor
   {:renderer render
    :subscription-deps [:adgroups :adgroups-meta]
    :component-deps [:breadcrumbs
                     :date-range-picker
                     :adgroup-performances
                     :scatter-chart]}))
