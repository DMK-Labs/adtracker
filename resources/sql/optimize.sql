-- :name current-settings :? :1
-- :doc Selects latest version of settings for given nclient
SELECT
  naver_customer_id as "customer-id",
  budget,
  objective,
  targets,
  bid_limit as "bid-limit"
FROM optimize.setting
WHERE naver_customer_id = :customer-id;

-- :name -save-settings :! :n
INSERT INTO optimize.setting
(naver_customer_id, budget, objective, targets, bid_limit)
VALUES (:customer-id, :budget, :objective, :targets, :bid-limit)
ON CONFLICT (naver_customer_id) DO UPDATE SET
(naver_customer_id, budget, objective, targets, bid_limit)
= (:customer-id, :budget, :objective, :targets, :bid-limit);

-- :name -insert-estimates :! :n
INSERT INTO optimize.estimate
(customer_id, keyword_id, device, keyword_plus, bid, impressions, clicks, cost)
VALUES :t*:estimates;

-- :name -insert-click-marginals :! :n
INSERT INTO optimize.click_marginals
(customer_id, keyword_id, device, keywordplus, bid, impressions,
 clicks, cost, marginal_bid, marginal_impressions, marginal_clicks, marginal_cost,
 marginal_efficiency)
VALUES :t*:marginals;

-- :name -fetch-marginals :? :*
SELECT
  keyword_id AS "keyword-id",
  bid,
  impressions,
  clicks,
  cost,
  marginal_bid AS "marginal-bid",
  marginal_impressions AS "marginal-impressions",
  marginal_clicks AS "marginal-clicks",
  marginal_cost AS "marginal-cost",
  marginal_efficiency AS "marginal-efficiency"
FROM optimize.click_marginals
WHERE customer_id = :customer-id;

-- :name parent-campaign :? :1
SELECT *
FROM naver.campaign
WHERE id =
      (SELECT campaign_id
       FROM naver.ad_group
       WHERE id = :adgroup-id);

-- :name insert-kw-estimates :! :n
INSERT INTO optimize.kw_estimates (key, device, keywordplus, bid, impressions, clicks, cost)
VALUES :t*:estimates;

-- :name kw-estimates :? :*
SELECT *
FROM optimize.kw_estimates;

-- :name estimates :? :*
SELECT
  customer_id  AS "customer-id",
--   adgroup_id   AS "adgroup-id",
  keyword_id   AS "key",
  device,
  keyword_plus AS "keywordplus",
  bid,
  impressions,
  clicks,
  cost
FROM optimize.estimate
WHERE customer_id = :customer-id;

