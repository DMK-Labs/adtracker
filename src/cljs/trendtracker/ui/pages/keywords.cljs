(ns trendtracker.ui.pages.keywords
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]
            [keechma.toolbox.ui :refer [sub> route>]]
            [reagent.core :as r]
            [trendtracker.ui.components.common :as common]
            [trendtracker.options :as opts]
            [trendtracker.utils :as u]))

(defn kw-title-renderer [customer-id]
  (fn [text record _]
    (r/as-element
     [:a {:href (str "https://manage.searchad.naver.com/customers/"
                     customer-id "/searchs?q="
                     (-> record
                         (js->clj :keywordize-keys true)
                         :keyword_id)
                     "&exact=false")
          :target "_blank"}
      text])))

(def krw-render (comp u/krw int))

(defn rank-render [rank]
  (if (nil? rank) "-" (u/dec-fmt 2 rank)))

(defn kw-columns [customer-id]
  (cons {:title "Keyword" :dataIndex :keyword
         :render (kw-title-renderer customer-id)
         :fixed "left"}
        (map #(assoc %
                :className "numbers"
                :sorter (u/sorter-by (:dataIndex %)))
             [{:title "노출순위" :dataIndex :avg-rank :render rank-render}
              {:title "노출수" :dataIndex :impressions :render u/int-fmt}
              {:title "CPM" :dataIndex :cpm :render krw-render}
              {:title "클릭수" :dataIndex :clicks :render u/int-fmt}
              {:title "클릭률" :dataIndex :ctr :render u/pct-fmt}
              {:title "CPC" :dataIndex :cpc :render krw-render}
              {:title "전환수" :dataIndex :conversions :render u/int-fmt}
              {:title "전환률" :dataIndex :cvr :render u/pct-fmt}
              {:title "CPA" :dataIndex :cpa :render krw-render}
              {:title "비용" :dataIndex :cost :render krw-render}
              {:title "전환매출" :dataIndex :revenue :render u/krw}
              {:title "이윤" :dataIndex :profit :render u/krw
               :fixed "right"}])))

(defn- on-check
  "Creates function to toggle inclusion of keywords with zero clicks in table."
  [ctx]
  (fn []
    (ui/redirect ctx (let [route (route> ctx)]
                       (if (:w-zero-click route)
                         (dissoc route :w-zero-click)
                         (assoc route :w-zero-click true))))))

(defn remove-clickless [keywords]
  (remove #(zero? (:clicks %)) keywords))

(defn render [ctx]
  (let [breadcrumbs (ui/component ctx :breadcrumbs)
        date-range-picker (ui/component ctx :date-range-picker)
        keyword-tool (ui/component ctx :keyword-tool)
        keywords (sub> ctx :keywords)
        keywords-meta (sub> ctx :keywords-meta)
        customer-id (:customer_id (sub> ctx :current-client))
        route (route> ctx)
        adding-new? (= "add-new" (:subpage route))
        result? (:result route)
        incl-clickless? (:w-zero-click route)]
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
             :style {:box-shadow "0 0 10px rgba(0,0,0,0.15)"}}]]
          [ant/col
           [ant/row {:type "flex" :justify "end" :gutter 8 :align "middle"}
            [ant/col [ant/input {:placeholder "키워드 검색" :prefix (r/as-element [ant/icon {:type "search"}])}]]
            [ant/col [ant/checkbox {:onChange (on-check ctx)
                                    :value incl-clickless?}
                      "클릭 미발생 포함"]]
            [ant/col [ant/checkbox {:onChange #()}
                      "동일 키워드 합산"]]
            [ant/col [ant/button {:icon "download"}]]]]])
       (if result?
         [(ui/component ctx :keyword-tool-results-table)]
         [ant/row
          [ant/col
           [ant/table
            {;; :bordered true
             :dataSource (if incl-clickless?
                           keywords
                           (remove-clickless keywords))
             :columns (kw-columns customer-id)
             :loading (= :pending (:status keywords-meta))
             :rowKey :keyword_id
             :size "small"
             :scroll {:x "1250px"}
             :rowSelection {:fixed true}
             :pagination (assoc opts/pagination :defaultPageSize 15)}]]])]]]))

(def component
  (ui/constructor
   {:renderer render
    :component-deps [:breadcrumbs :date-range-picker
                     :keyword-tool :keyword-tool-results-table]
    :subscription-deps [:keywords :keywords-meta
                        :current-client]}))
