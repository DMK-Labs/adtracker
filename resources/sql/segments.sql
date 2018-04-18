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
  keyword_id,
  keyword,
  sum(ad_rank_sum)                    AS ad_rank_sum,
  sum(cost)                           AS cost,
  sum(impressions)                    AS impressions,
  sum(clicks)                         AS clicks,
  sum(conversions)                    AS conversions,
  sum(revenue)                        AS revenue,
  sum(revenue) - sum(cost)            AS profit
FROM naver.daily_keyword_stats
WHERE customer_id = :customer-id and
      keyword NOTNULL and
      during BETWEEN :low ::TIMESTAMP AND :high ::TIMESTAMP
GROUP BY keyword, keyword_id
ORDER BY profit DESC;
