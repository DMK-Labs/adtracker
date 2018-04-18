-- :name powerlink-ads :? :*
SELECT
  ad_id                                    AS "ad-id",
  ad.subject,
  ad.description,
  ad.is_off,
  ad.pc_landing_url,
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
         AND during BETWEEN :low :: TIMESTAMP AND :high :: TIMESTAMP
         AND campaign_id IN (SELECT id
                             FROM naver.campaign
                             WHERE campaign_type_id = 1)
   GROUP BY ad_id) AS res
  LEFT JOIN naver.ad ad ON res.ad_id = ad.id
  LEFT JOIN naver.ad_group ag ON ad.ad_group_id = ag.id
WHERE impressions > 0
      AND ad.subject NOTNULL
ORDER BY ctr DESC;
