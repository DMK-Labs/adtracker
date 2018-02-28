-- :name parent-campaign :? :1
SELECT *
FROM naver.campaign WHERE id =
(SELECT campaign_id
 FROM naver.ad_group
 WHERE id = :adgroup-id)
