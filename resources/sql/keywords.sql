-- :name all :? :*
select
keyword,
id,
bid,
ad_group_id as "adgroup-id",
pc_landing_url as "pc-landing",
mobile_landing_url as "mobile-landing"
from naver.keyword
where customer_id = :customer-id and is_off IS FALSE and del_at IS NULL
