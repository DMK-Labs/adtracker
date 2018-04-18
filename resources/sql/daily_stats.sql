-- :name by-adgroup :? :*
SELECT
  cal.day :: DATE          AS during,
  coalesce(cost, 0)        AS cost,
  coalesce(impressions, 0) AS impressions,
  coalesce(clicks, 0)      AS clicks,
  coalesce(ad_rank_sum, 0) AS ad_rank_sum,
  coalesce(conversions, 0) AS conversions,
  coalesce(revenue, 0)     AS revenue
FROM
  (SELECT generate_series(:low :: TIMESTAMP, :high :: TIMESTAMP, INTERVAL '1 day') AS day)
    AS cal
  LEFT JOIN
  (SELECT
     during,
     sum(cost)        AS cost,
     sum(impressions) AS impressions,
     sum(clicks)      AS clicks,
     sum(ad_rank_sum) AS ad_rank_sum,
     sum(conversions) AS conversions,
     sum(revenue)     AS revenue
   FROM naver.daily_keyword_stats
   WHERE customer_id = :customer-id AND
         ad_group_id = :adgroup-id
   GROUP BY during) AS p
    ON cal.day = p.during
ORDER BY during ASC;

-- :name by-campaign :? :*
SELECT
  cal.day :: DATE          AS during,
  coalesce(cost, 0)        AS cost,
  coalesce(impressions, 0) AS impressions,
  coalesce(clicks, 0)      AS clicks,
  coalesce(ad_rank_sum, 0) AS ad_rank_sum,
  coalesce(conversions, 0) AS conversions,
  coalesce(revenue, 0)     AS revenue
FROM
  (SELECT generate_series(:low :: TIMESTAMP, :high :: TIMESTAMP, INTERVAL '1 day') AS day)
    AS cal
  LEFT JOIN
  (SELECT
     during,
     sum(cost)        AS cost,
     sum(impressions) AS impressions,
     sum(clicks)      AS clicks,
     sum(ad_rank_sum) AS ad_rank_sum,
     sum(conversions) AS conversions,
     sum(revenue)     AS revenue
   FROM naver.daily_keyword_stats
   WHERE customer_id = :customer-id AND
         campaign_id = :campaign-id
   GROUP BY during)
    AS p
    ON cal.day = p.during
ORDER BY during ASC;

-- :name by-type :? :*
SELECT
  cal.day :: DATE          AS during,
  coalesce(cost, 0)        AS cost,
  coalesce(impressions, 0) AS impressions,
  coalesce(clicks, 0)      AS clicks,
  coalesce(ad_rank_sum, 0) AS ad_rank_sum,
  coalesce(conversions, 0) AS conversions,
  coalesce(revenue, 0)     AS revenue
FROM
  (SELECT generate_series(:low :: TIMESTAMP, :high :: TIMESTAMP, INTERVAL '1 day') AS day)
    AS cal
  LEFT JOIN
  (SELECT
     during,
     sum(cost)        AS cost,
     sum(impressions) AS impressions,
     sum(clicks)      AS clicks,
     sum(ad_rank_sum) AS ad_rank_sum,
     sum(conversions) AS conversions,
     sum(revenue)     AS revenue
   FROM naver.daily_keyword_stats
   WHERE campaign_id IN
         (-- List of campaigns that are of a certain type, defined below
           SELECT id
           FROM naver.campaign
           WHERE campaign_type_id IN
                 (-- list of campaign IDs of a given type, 'powerlink' or 'shopping', etc.
                   SELECT id
                   FROM naver.campaign_type
                   WHERE NAME = :campaign-type)) AND
         customer_id = :customer-id
   GROUP BY during
  ) AS p
    ON cal.day = p.during
ORDER BY during ASC;

-- :name by-customer :? :*
SELECT
  cal.day :: DATE            AS during,
  coalesce(p.cost, 0)        AS cost,
  coalesce(p.impressions, 0) AS impressions,
  coalesce(p.clicks, 0)      AS clicks,
  coalesce(p.ad_rank_sum, 0) AS ad_rank_sum,
  coalesce(p.conversions, 0) AS conversions,
  coalesce(p.revenue, 0)     AS revenue
FROM
  (SELECT generate_series(:low :: TIMESTAMP, :high :: TIMESTAMP, INTERVAL '1 day')
    AS day)
    AS cal
  LEFT JOIN
  (SELECT
     during,
     sum(cost)        AS cost,
     sum(impressions) AS impressions,
     sum(clicks)      AS clicks,
     sum(ad_rank_sum) AS ad_rank_sum,
     sum(conversions) AS conversions,
     sum(revenue)     AS revenue
   FROM naver.daily_keyword_stats
   WHERE customer_id = :customer-id
   GROUP BY during, customer_id) AS p
    ON cal.day = p.during
ORDER BY during ASC;

-- :name last-recorded-conversion :? :1
SELECT during
FROM naver.conversion
WHERE customer_id = :customer-id
ORDER BY during DESC
LIMIT 1;

-- :name last-recorded-effectiveness :? :1
SELECT during
FROM naver.effectiveness
WHERE customer_id = :customer-id
ORDER BY during DESC
LIMIT 1;

-- :name last-recorded-foo :? :1
SELECT during
FROM :i:table
WHERE customer_id = :customer-id
ORDER BY during DESC
LIMIT 1;

-- :name first-recorded-performance :? :1
SELECT during
FROM naver.daily_keyword_stats
WHERE customer_id = :customer-id
ORDER BY during ASC
LIMIT 1;
