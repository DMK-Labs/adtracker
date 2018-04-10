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
LIMIT 15;

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
