-- :name kwid-ctr-cvr :? :1
-- :doc takes a single keyword-id, returns ctr and cvr historically. If no record, returns 0
SELECT *
FROM naver.keyword_id_funnel
WHERE keyword_id = :id;


-- :name global-funnel :? :*
SELECT
  keyword_id,
  keyword,
  sum(impressions) :: INT                                                         AS impressions,
  sum(clicks) :: INT                                                              AS clicks,
  sum(conversions) :: INT                                                         AS conversions,
  coalesce(sum(clicks) / nullif(sum(impressions), 0), 0) :: DOUBLE PRECISION      AS ctr,
  coalesce(sum(conversions) / nullif(sum(clicks), 0), 0) :: DOUBLE PRECISION      AS cvr,
  coalesce(sum(conversions) / nullif(sum(impressions), 0), 0) :: DOUBLE PRECISION AS i2c
FROM naver.daily_keyword_stats
WHERE customer_id = :customer-id AND keyword NOTNULL
GROUP BY keyword, keyword_id
ORDER BY ctr DESC;
