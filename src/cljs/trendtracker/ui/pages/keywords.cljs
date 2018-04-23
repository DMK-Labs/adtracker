(ns trendtracker.ui.pages.keywords
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> route>]]
            [reagent.core :as r]
            [trendtracker.ui.components.common :as common]
            [trendtracker.options :as opts]
            [trendtracker.utils :as u]
            [trendtracker.ui.components.pure.form-inputs :refer [controlled-input]]
            [keechma.toolbox.forms.ui :as forms-ui]))

(defn- toggle
  "Creates function to toggle inclusion of keywords with zero clicks in table."
  [ctx k]
  (fn []
    (ui/redirect ctx (let [route (route> ctx)]
                       (if (k route)
                         (dissoc route k)
                         (assoc route k true))))))

(defn keyword-search [ctx]
  (let [form-props [:keyword-search :form]
        form-state (forms-ui/form-state> ctx form-props)]
    [ant/form {:on-submit #(forms-ui/<submit ctx form-props %)}
     [controlled-input ctx form-props :keyword-search {:form-state form-state
                                                       ;; :label "search"
                                                       :placeholder "keyword search"}]]))


(defn render [ctx]
  (let [breadcrumbs (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)
        keyword-tool (ui/component ctx :keyword-tool)
        keyword-performances (ui/component ctx :keyword-performances)
        keyword-tool-results (ui/component ctx :keyword-tool-results-table)
        route (route> ctx)
        adding-new? (= "add-new" (:subpage route))]
    [:div
     [common/content-header
      [breadcrumbs]
      [ant/row {:type "flex" :justify "space-between"}
       [ant/col {:md 7 :xs 24}
        [:h2.page-title "키워드 Ranking"]]
       [ant/col [ant/col [date-range-picker]]]]]
     [:div.content
      [ant/card
       (if adding-new?
         [:div {:style {:margin-bottom 16
                        :padding-bottom 16
                        :border-bottom "1px solid #e8e8e8"}}
          [keyword-tool]]
         [ant/row {:type "flex" :justify "space-between" :style {:margin-bottom 8} :align "middle"}
          [ant/col
           [ant/button
            {:type :primary :shape :circle :icon :plus :size :large
             :on-click #(ui/redirect ctx (assoc route :subpage "add-new"))
             :style {:box-shadow "0 0 10px rgba(0,0,0,0.25), 0 0 10px rgba(0,0,0,0.15)"}}]]
          [ant/col
           [ant/row {:type "flex" :justify "end" :gutter 8 :align "middle"}
            [ant/col
             #_[ant/input
                {:placeholder "키워드 검색" :prefix (r/as-element [ant/icon {:type "search"}])}]
             [keyword-search ctx]]
            [ant/col
             [ant/checkbox
              {:onChange (toggle ctx :zero-clicks)
               :checked (:zero-clicks route)}
              "클릭 미발생 포함"]]
            [ant/col
             [ant/checkbox
              {:onChange (toggle ctx :by-kw)
               :checked (:by-kw route)}
              "키워드명 기준"]]
            [ant/col
             [ant/button {:icon "download"}]]]]])
       (if (:result route)
         [keyword-tool-results]
         [ant/row
          [ant/col
           [keyword-performances (:by-kw route)]]])]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs :date-range-picker
                     :keyword-tool :keyword-tool-results-table
                     :keyword-performances]}))
