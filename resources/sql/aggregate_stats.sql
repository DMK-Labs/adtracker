-- :name kwid-ctr-cvr :? :1
-- :doc takes a single keyword-id, returns ctr and cvr historically. If no record, returns 0
SELECT *
FROM naver.keyword_id_funnel
WHERE keyword_id = :id;


-- :name funnel-by-kwid :? :*
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


-- :name funnel-by-keyword :? :*
SELECT
  keyword,
  sum(impressions) :: INT                                                         AS impressions,
  sum(clicks) :: INT                                                              AS clicks,
  sum(conversions) :: INT                                                         AS conversions,
  coalesce(sum(clicks) / nullif(sum(impressions), 0), 0) :: DOUBLE PRECISION      AS ctr,
  coalesce(sum(conversions) / nullif(sum(clicks), 0), 0) :: DOUBLE PRECISION      AS cvr,
  coalesce(sum(conversions) / nullif(sum(impressions), 0), 0) :: DOUBLE PRECISION AS i2c
FROM naver.daily_keyword_stats
WHERE customer_id = :customer-id AND keyword NOTNULL
GROUP BY keyword
ORDER BY ctr DESC;

-- :name by-adgroup :? :*
SELECT
  keyword_id,
  keyword,
  sum(cost) as cost,
  sum(impressions) as impressions,
  sum(clicks) as clicks,
  sum(ad_rank_sum) as ad_rank_sum,
  sum(conversions) as conversions,
  sum(revenue) as revenue
FROM naver.daily_keyword_stats
WHERE ad_group_id = :id AND during BETWEEN :low :: TIMESTAMP AND :high :: TIMESTAMP
GROUP BY keyword_id, keyword
