-- :name pc-mobile :? :*
SELECT
  pc_mobile_type,
  sum(cost) as cost,
  sum(impressions) as impressions,
  sum(clicks) as clicks,
  sum(ad_rank_sum) as ad_rank_sum,
  sum(conversions) as conversions,
  sum(revenue) as revenue
FROM naver.daily_keyword_stats
where customer_id = :customer-id and during BETWEEN :low ::TIMESTAMP AND :high ::TIMESTAMP
GROUP BY pc_mobile_type;


-- :name keywords :? :*
SELECT
  k.campaign_id            AS "campaign-id",
  c.name                   AS campaign,
  ad_group_id              AS "adgroup-id",
  a.name                   AS adgroup,
  keyword_id               AS "keyword-id",
  keyword,
  pc_mobile_type           AS device,
  sum(ad_rank_sum)         AS ad_rank_sum,
  sum(cost)                AS cost,
  sum(impressions)         AS impressions,
  sum(clicks)              AS clicks,
  sum(conversions)         AS conversions,
  sum(revenue)             AS revenue,
  sum(revenue) - sum(cost) AS profit
FROM naver.daily_keyword_stats k
  LEFT JOIN naver.campaign c ON k.campaign_id = c.id
  LEFT JOIN naver.ad_group a ON k.ad_group_id = a.id
WHERE k.customer_id = :customer-id
      AND keyword NOTNULL
      AND during BETWEEN :low :: TIMESTAMP AND :high :: TIMESTAMP
GROUP BY k.campaign_id, c.name, ad_group_id, a.name, keyword, keyword_id, pc_mobile_type
ORDER BY profit DESC;

-- :name adgroups :? :*
SELECT
  k.campaign_id            AS "campaign-id",
  c.name                   AS campaign,
  ad_group_id              AS "adgroup-id",
  a.name                   AS adgroup,
  sum(ad_rank_sum)         AS ad_rank_sum,
  sum(cost)                AS cost,
  sum(impressions)         AS impressions,
  sum(clicks)              AS clicks,
  sum(conversions)         AS conversions,
  sum(revenue)             AS revenue,
  sum(revenue) - sum(cost) AS profit
FROM naver.daily_keyword_stats k
  LEFT JOIN naver.campaign c ON k.campaign_id = c.id
  LEFT JOIN naver.ad_group a ON k.ad_group_id = a.id
WHERE k.customer_id = :customer-id
      AND keyword NOTNULL
      AND during BETWEEN :low :: TIMESTAMP AND :high :: TIMESTAMP
GROUP BY k.campaign_id, c.name, ad_group_id, a.name
ORDER BY profit DESC;
