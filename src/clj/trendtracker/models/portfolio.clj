(ns trendtracker.models.portfolio
  (:require [clojure.walk :as w]
            [com.rpl.specter :refer :all]
            [naver-searchad.api.adgroup :as adgroup]
            [naver-searchad.api.campaign :as campaign]
            [trendtracker.config :refer [config]]
            [huri.core :as h]))

(def creds (:naver-creds config))

(defn campaigns [n-id]
  (campaign/all (assoc creds :customer-id n-id)))

(defn filter-eligible [maps]
  (h/where {:status "ELIGIBLE"} maps))

(defn assoc-adgroup [campaign-map]
  (let [new-creds (assoc creds :customer-id (:customerId campaign-map))
        adgrps (->> (:nccCampaignId campaign-map)
                    (adgroup/get-by-campaign-id new-creds)
                    :body
                    ;; filter-eligible
                    (map #(select-keys % [:nccAdgroupId :name])))]
    (-> campaign-map
        (assoc :children adgrps)
        (dissoc :customerId))))

(defn rename [rel]
  (w/postwalk (fn [x]
                (case x
                  :nccAdgroupId  :value
                  :name          :label
                  :nccCampaignId :value
                  "SHOPPING"     "쇼핑"
                  :SHOPPING      :shopping
                  "WEB_SITE"     "파워링크"
                  :WEB_SITE      :powerlink
                  "BRAND_SEARCH" "브랜드"
                  :BRAND_SEARCH  :brand
                  :campaignTp    :campaign-type
                  :expectCost    :expected-cost
                  x))
              rel))

(defn tree [n-id]
  (let [eligible (->> (campaigns n-id)
                      ;; filter-eligible
                      (map #(select-keys % [:campaignTp :name :nccCampaignId :customerId])))
        raw-tree (map (fn [[k v]]
                        {:value (keyword k)
                         :label k
                         :children (map #(dissoc % :campaignTp) v)})
                      (group-by :campaignTp (map assoc-adgroup eligible)))
        renamed-tree (rename raw-tree)]
    (conj renamed-tree {:value :total :label "모든 검색광고"})))

(defn optimizing
  "The subset of the portfolio tree which is currently eligible to be optimized,
  with relevant info."
  [n-id]
  (let [children (->> (campaigns n-id)
                      (h/where {:campaignTp "WEB_SITE"})
                      (map #(select-keys % [:nccCampaignId :expectCost :name :campaignTp :status]))
                      (map #(assoc % :optimizing? false))
                      rename
                      (sort-by :status))]
    children))


(comment
  (tree 1334028)
  (optimizing 137307))
  ;; =>
  ;; [{:value "powerlink",
  ;;   :name "ALL",
  ;;   :status :on,
  ;;   :campaign-type "파워링크",
  ;;   :expected-cost 1485,
  ;;   :children ({:value "cmp-a001-01-000000000243172",
  ;;               :expected-cost 418,
  ;;               :label "4.프라덤",
  ;;               :campaign-type "파워링크"}
  ;;              {:value "cmp-a001-01-000000000594116",
  ;;               :expected-cost 990,
  ;;               :label "1.프라젠트라Mobile",
  ;;               :campaign-type "파워링크"}
  ;;              {:value "cmp-m001-01-000000016892708",
  ;;               :expected-cost 77,
  ;;               :label "1.프라젠트라PC",
  ;;               :campaign-type "파워링크"})}]

