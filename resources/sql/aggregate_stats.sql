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
GROUP BY keyword_id, keyword;

-- :name adgroups :? :*
SELECT
  ad_group_id AS "adgroup-id",
  ad_group.name,
  sum(cost)        AS cost,
  sum(impressions) AS impressions,
  sum(clicks)      AS clicks,
  sum(ad_rank_sum) AS ad_rank_sum,
  sum(conversions) AS conversions,
  sum(revenue)     AS revenue
FROM naver.daily_keyword_stats k
  LEFT JOIN naver.ad_group ON ad_group_id = ad_group.id
WHERE k.customer_id = :customer-id
  AND during BETWEEN :low ::TIMESTAMP AND :high ::TIMESTAMP
GROUP BY ad_group_id, ad_group.name;

-- :name recent-keyword-performance :? :*
SELECT
  customer_id                                                                 AS "customer-id",
  --   campaign_id,
  ad_group_id                                                                 AS "adgroup-id",
  keyword_id                                                                  AS "keyword-id",
  keyword,
  pc_mobile_type                                                              AS "device",
  sum(cost) :: INT                                                            AS cost,
  sum(impressions) :: INT                                                     AS impressions,
  sum(clicks) :: INT                                                          AS clicks,
  sum(ad_rank_sum) :: INT                                                     AS "ad-rank-sum",
  round(coalesce(sum(ad_rank_sum) /
                 nullif(sum(impressions), 0), 0)
        :: NUMERIC, 1) :: DOUBLE PRECISION                                    AS "avg-rank",
  sum(conversions) :: INT                                                     AS conversions,
  coalesce(sum(conversions) / nullif(sum(clicks), 0), 0) :: DOUBLE PRECISION  AS cvr,
  sum(revenue) :: INT                                                         AS revenue,
  sum(revenue) - sum(cost) :: INT                                             AS profit,
  coalesce(sum(cost) / nullif(sum(clicks), 0), 0) :: DOUBLE PRECISION         AS "avg-cpc",
  coalesce(sum(revenue) / nullif(sum(conversions), 0), 0) :: DOUBLE PRECISION AS "conversion-revenue",
  coalesce(sum(revenue) / nullif(sum(clicks), 0), 0) :: DOUBLE PRECISION      AS "click-revenue"
FROM naver.daily_keyword_stats
WHERE during >= current_date - :days :: INT
      AND customer_id = :customer
GROUP BY customer_id, campaign_id, ad_group_id, keyword_id, keyword, pc_mobile_type;
