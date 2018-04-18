(ns naver-searchad.api.adkeyword
  (:require [naver-searchad.api.adgroup :as adgroup]
            [naver-searchad.api.request :as request]))

(defn get-by-adgroup [creds id]
  (:body (request/GET creds "/ncc/keywords" {:query-params {:nccAdgroupId id}})))

(defn get-by-campaign [creds ids]
  (mapcat (partial get-by-adgroup creds)
          (map :nccAdgroupId (mapcat #(:body (adgroup/get-by-campaign creds %)) ids))))

(defn get-by-id [creds ids]
  (:body (request/GET creds "/ncc/keywords" {:query-params {:ids ids}})))

(defn get-all
  "Aggregator function that returns a flat vector of every AdKeyword."
  [creds]
  (let [group-ids (map :nccAdgroupId (adgroup/all creds))]
    (mapcat #(get-by-adgroup creds %) group-ids)))

(defn update-keyword!
  "http://naver.github.io/searchad-apidoc/#/operations/PUT/~2Fncc~2Fkeywords~2F%7BnccKeywordId%7D%7B%3Ffields%7D"
  [creds keyword-id params]
  (request/PUT
   creds
   (str "/ncc/keywords/" keyword-id)
   params))

(defn update-keyword-bid!
  "Body should be a map with following keys: [:keyword-id, :adgroup-id, :bid]"
  [creds {:keys [keyword-id adgroup-id bid]}]
  (update-keyword! creds keyword-id
                   {:body {:nccKeywordId keyword-id
                           :nccAdgroupId adgroup-id
                           :bidAmt bid
                           :useGroupBidAmt false}
                    :query-params {:fields "bidAmt"}}))
