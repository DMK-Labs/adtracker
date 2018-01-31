-- :name current-settings :? :1
-- :doc Selects latest version of settings for given nclient
SELECT *
FROM optimize.setting
WHERE naver_customer_id = :customer-id
ORDER BY version DESC LIMIT 1;

-- :name estimates :? :*
SELECT customer_id as "customer-id",
       adgroup_id as "adgroup-id",
       keyword_id as "key",
       device,
       keyword_plus as "keywordplus",
       bid,
       impressions,
       clicks,
       cost
FROM naver.estimate WHERE customer_id = :customer-id;


-- :name insert-click-marginals :! :n
INSERT INTO optimize.click_marginals
(customer_id, campaign_id, adgroup_id, keyword_id, device, keywordplus, bid, impressions,
clicks, cost, marginal_bid, marginal_impressions, marginal_clicks, marginal_cost,
marginal_efficiency)
VALUES
  (:customer-id, :campaign-id, :adgroup-id, :keyword-id, :device, :keywordplus, :bid,
  :impressions, :clicks, :cost, :marginal-bid, :marginal-impressions, :marginal-clicks,
  :marginal-cost, :marginal-efficiency);


-- :name parent-campaign :? :1
SELECT *
FROM naver.campaign WHERE id =
(SELECT campaign_id
 FROM naver.ad_group
 WHERE id = :adgroup-id)


-- :name insert-kw-estimates :! :n
INSERT INTO optimize.kw_estimates (key, device, keywordplus, bid, impressions, clicks, cost)
VALUES :t*:estimates


-- :name kw-estimates :? :*
select * from optimize.kw_estimates
