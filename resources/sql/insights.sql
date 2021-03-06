-- :name no-clicks :? :*
SELECT *
FROM
  (SELECT
     keyword_id,
     keyword,
     sum(impressions) AS impressions,
     sum(clicks)      AS clicks
   FROM naver.daily_keyword_stats
   WHERE customer_id = :customer
         AND during >= current_date - INTERVAL '2' MONTH
   GROUP BY keyword_id, keyword) AS recent_two_months
WHERE clicks = 0
ORDER BY impressions DESC
LIMIT 30;

-- :name biggest-losers :? :*
SELECT *
FROM
  (SELECT
     keyword,
     sum(cost)                                            AS cost,
     sum(impressions)                                     AS impressions,
     sum(clicks)                                          AS clicks,
     sum(conversions)                                     AS conversions,
     sum(revenue) :: INTEGER                              AS revenue,
     sum(revenue) - sum(cost) :: INTEGER                  AS profit,
     round(sum(cost) / nullif(sum(clicks), 0)) :: INTEGER AS cpc
   FROM naver.daily_keyword_stats
   WHERE customer_id = :customer
         AND during >= current_date - INTERVAL '2' MONTH
   GROUP BY keyword) AS x
WHERE keyword NOTNULL
ORDER BY profit ASC
LIMIT 15;

-- :name best-powerlink-ads :? :*
SELECT
  ad_id                                    AS "ad-id",
  ad.subject,
  ad.description,
  ad.is_off,
  ag.id                                    AS "adgroup-id",
  ag.name                                  AS adgroup,
  cost,
  impressions,
  clicks,
  clicks :: DOUBLE PRECISION / impressions AS ctr
FROM
  (SELECT
     ad_id,
     sum(cost)        AS cost,
     sum(impressions) AS impressions,
     sum(clicks)      AS clicks
   FROM naver.effectiveness
   WHERE customer_id = :customer
         AND campaign_id IN (SELECT id
                             FROM naver.campaign
                             WHERE campaign_type_id = 1)
   GROUP BY ad_id) AS res
  LEFT JOIN naver.ad ad ON res.ad_id = ad.id
  LEFT JOIN naver.ad_group ag ON ad.ad_group_id = ag.id
WHERE impressions > 100
      AND clicks > 0
      AND ad.subject NOTNULL
ORDER BY ctr DESC;

