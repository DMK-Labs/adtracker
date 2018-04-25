(ns trendtracker.ui.pages.creatives
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> route>]]
            [reagent.core :as r]
            [trendtracker.ui.components.common :as common]
            [trendtracker.options :as opts]
            [trendtracker.utils :as u]
            [keechma.toolbox.forms.ui :as forms-ui]
            [trendtracker.ui.components.pure.form-inputs :refer [controlled-input]]
            [clojure.string :as string]))

(defn ad-render [customer-id]
  (fn [_ record _]
    (let [record-map (js->clj record :keywordize-keys true)]
      (r/as-element
       [:div
        [:a {:href (str "https://manage.searchad.naver.com/customers/"
                        customer-id "/searchs?q="
                        (:ad-id record-map)
                        "&exact=false")
             :target "_blank"}
         (:subject record-map)]
        [:div (:description record-map)]]))))

(defn adgroup-render [customer-id]
  (fn [text record _]
    (let [record-map (js->clj record :keywordize-keys true)]
      (r/as-element
       [:a {:href (str "https://manage.searchad.naver.com/customers/"
                       customer-id "/adgroups/"
                       (:adgroup-id record-map))
            :target "_blank"}
        text]))))

(defn url-render [text _ _]
  (r/as-element
   [:a {:href text
        :target "_blank"}
    text]))

(defn creative-search [ctx]
  (let [form-props [:creative-search :form]
        form-state (forms-ui/form-state> ctx form-props)]
    [ant/form {:on-submit #(forms-ui/<submit ctx form-props %)}
     [controlled-input ctx form-props :creative-search
      {:form-state form-state
       ;; :label "search"
       :placeholder "광고 검색"
       :item-opts {:style {:margin-bottom 0}}
       :input-opts {:prefix (r/as-element [ant/icon {:type "search"}])}}]]))

(defn columns [customer-id]
  [{:title "광고소재" :dataIndex :ad-id :render (ad-render customer-id)
    :width 500 :fixed true}
   {:title "광고그룹" :dataIndex :adgroup :render (adgroup-render customer-id)}
   {:title "URL" :dataIndex :pc_landing_url :render url-render}
   {:title "비용" :dataIndex :cost :sorter (u/sorter-by :cost) :render u/krw}
   {:title "노출수" :dataIndex :impressions :sorter (u/sorter-by :impressions) :render u/int-fmt}
   {:title "클릭수" :dataIndex :clicks :sorter (u/sorter-by :clicks) :render u/int-fmt}
   {:title "CTR" :dataIndex :ctr :sorter (u/sorter-by :ctr) :render u/pct-fmt}])

(defn render [ctx]
  (let [breadcrumbs (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)
        best-ads (sub> ctx :creatives)
        creatives-meta (sub> ctx :creatives-meta)
        customer-id (:customer_id (sub> ctx :current-client))
        route (route> ctx)
        regex (if-let [q (:cq route)]
                (re-pattern q)
                #"")]
    [:div
     [common/content-header
      [breadcrumbs]
      [ant/row {:type "flex" :justify "space-between"}
       [ant/col {:md 7 :xs 24}
        [:h2.page-title "광고소재 Ranking"]]
       [ant/col [ant/col [date-range-picker]]]]
      [:div "현 계정에서 가장 좋은 성과를 보인 광고소재들입니다."]]
     [:div.content
      [ant/card
       [ant/row {:type "flex" :justify "end" :gutter 8 :align "middle" :style {:margin-bottom 8}}
        [ant/col [creative-search ctx]]
        #_[ant/col [ant/input {:placeholder "광고소재 검색" :prefix (r/as-element [ant/icon {:type "search"}])}]]
        [ant/col [ant/button {:icon "download"}]]]
       (when (:cq route)
         [:div {:style {:margin-bottom 16}}
          [ant/divider {:style {:margin "16px 0"}}]
          [:div [ant/icon {:type "filter" :style {:margin-right 4}}] "검색 필터: "
           [ant/tag
            {:closable true :color "blue" :onClose #(ui/redirect ctx (dissoc route :cq))
             :onClick #(ui/redirect ctx (dissoc route :cq))}
            (:cq route)]]])
       #_[:div {:style {:margin-bottom 16}}

          [ant/divider {:style {:margin "16px 0"}}]]

       [ant/table
        {:bordered true
         :dataSource (filter
                      #(or (re-find regex (apply str (vals %)))
                           (re-find regex (string/lower-case (apply str (vals %)))))
                      best-ads)
         :loading (= :pending (:status creatives-meta))
         :columns (columns customer-id)
         :rowKey :ad-id
         :size "small"
         :scroll {:x "1150px"}
         :pagination opts/pagination}]]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs :date-range-picker]
    :subscription-deps [:creatives :creatives-meta
                        :current-client]}))

