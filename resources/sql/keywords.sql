-- :name all :? :*
SELECT
  a.campaign_id AS "campaign-id",
  k.ad_group_id AS "adgroup-id",
  k.id,
  k.keyword,
  k.bid
FROM naver.keyword AS k LEFT JOIN naver.ad_group AS a ON k.ad_group_id = a.id
WHERE k.customer_id = :customer-id AND k.is_off IS FALSE AND k.del_at IS NULL

-- :name by-campaign-id :? :*
SELECT
  a.campaign_id AS "campaign-id",
  k.ad_group_id AS "adgroup-id",
  k.id,
  k.keyword,
  k.bid
FROM naver.keyword AS k LEFT JOIN naver.ad_group AS a ON k.ad_group_id = a.id
WHERE
  k.customer_id = :customer-id AND k.is_off IS FALSE AND k.del_at IS NULL AND a.campaign_id IN (:v*:campaign-ids);

-- :name -parent :? :1
SELECT ad_group_id AS "adgroup-id"
FROM naver.keyword
WHERE id = :id;

-- :name -owner :? :1
SELECT customer_id AS "customer-id"
FROM naver.keyword
WHERE id = :id;

  -- :name name :? :1
  select keyword AS "name"
FROM naver.keyword WHERE id = :id
