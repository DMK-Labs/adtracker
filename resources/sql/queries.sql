-- resources/sql/queries.sql
-- Adgroup/Campaign/Type Performance Queries

-- :name adgrp-perf-by-id-date :? :*
-- :doc Selects daily adgroup perfs by date and id filters.
SELECT
  cal.day :: DATE            AS during,
  coalesce(p.impressions, 0) AS impressions,
  coalesce(p.clicks, 0)      AS clicks,
  coalesce(p.cost, 0)        AS cost,
  coalesce(p.ad_rank_sum, 0) AS ad_rank_sum,
  coalesce(p.conversions, 0) AS conversions,
  coalesce(p.revenue, 0)     AS revenue,
  coalesce(p.ctr, 0)         AS ctr,
  coalesce(p.cvr, 0)         AS cvr,
  coalesce(p.roas, 0)        AS roas,
  coalesce(p.profit, 0)      AS profit
FROM
  (SELECT generate_series(:low :: TIMESTAMP, :high :: TIMESTAMP, INTERVAL '1 day') AS day) AS cal
  LEFT JOIN (SELECT *
             FROM naver.adgroup_performance
             WHERE customer_id = :customer-id
             AND ad_group_id = :id) AS p
    ON cal.day = p.during
ORDER BY during ASC;

-- :name cmp-perf-by-id-date :? :*
-- :doc Selects daily campaign perfs by date and id filters.
SELECT
  cal.day :: DATE            AS during,
  coalesce(p.impressions, 0) AS impressions,
  coalesce(p.clicks, 0)      AS clicks,
  coalesce(p.cost, 0)        AS cost,
  coalesce(p.ad_rank_sum, 0) AS ad_rank_sum,
  coalesce(p.conversions, 0) AS conversions,
  coalesce(p.revenue, 0)     AS revenue,
  coalesce(p.ctr, 0)         AS ctr,
  coalesce(p.cvr, 0)         AS cvr,
  coalesce(p.roas, 0)        AS roas,
  coalesce(p.profit, 0)      AS profit
FROM
  (SELECT generate_series(:low :: TIMESTAMP, :high :: TIMESTAMP, INTERVAL '1 day') AS day) AS cal
  LEFT JOIN (SELECT *
             FROM naver.campaign_performance
             WHERE customer_id = :customer-id
             AND campaign_id = :id) AS p
    ON cal.day = p.during
ORDER BY during ASC;

-- :name cmp-type-perf :? :*
-- :doc Selects daily campaign-type perfs by date and name filters.
SELECT
  cal.day :: DATE            AS during,
  coalesce(p.impressions, 0) AS impressions,
  coalesce(p.clicks, 0)      AS clicks,
  coalesce(p.cost, 0)        AS cost,
  coalesce(p.ad_rank_sum, 0) AS ad_rank_sum,
  coalesce(p.conversions, 0) AS conversions,
  coalesce(p.revenue, 0)     AS revenue,
  coalesce(p.ctr, 0)         AS ctr,
  coalesce(p.cvr, 0)         AS cvr,
  coalesce(p.roas, 0)        AS roas,
  coalesce(p.profit, 0)      AS profit
FROM
  (SELECT generate_series(:low :: TIMESTAMP, :high :: TIMESTAMP, INTERVAL '1 day') AS day) AS cal
  LEFT JOIN (SELECT *
             FROM naver.campaign_type_performance
             WHERE customer_id = :customer-id
             AND campaign_type = :type) AS p
    ON cal.day = p.during
ORDER BY during ASC;

-- :name total-perf-by-date :? :*
-- :doc Selects daily total perfs by date.
SELECT
  cal.day :: DATE            AS during,
  coalesce(p.impressions, 0) AS impressions,
  coalesce(p.clicks, 0)      AS clicks,
  coalesce(p.cost, 0)        AS cost,
  coalesce(p.ad_rank_sum, 0) AS ad_rank_sum,
  coalesce(p.conversions, 0) AS conversions,
  coalesce(p.revenue, 0)     AS revenue,
  coalesce(p.ctr, 0)         AS ctr,
  coalesce(p.cvr, 0)         AS cvr,
  coalesce(p.roas, 0)        AS roas,
  coalesce(p.profit, 0)      AS profit
FROM
  (SELECT generate_series(:low :: TIMESTAMP, :high :: TIMESTAMP, INTERVAL '1 day') AS day) AS cal
  LEFT JOIN (SELECT *
             FROM naver.total_performance
             WHERE customer_id = :customer-id) AS p
    ON cal.day = p.during
ORDER BY during ASC;
