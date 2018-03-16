-- :name by-customer-id :? :*
SELECT
  keyword_id   AS "keyword-id",
  keyword_plus AS "keywordplus",
  device,
  bid,
  impressions,
  clicks,
  cost
FROM optimize.estimate
WHERE customer_id = :id
