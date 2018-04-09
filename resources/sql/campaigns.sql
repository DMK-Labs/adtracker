-- :name -all :? :*
SELECT *
FROM naver.campaign
WHERE customer_id = :customer-id AND del_at IS NULL;

-- :name -find :? :1
SELECT *
FROM naver.campaign
WHERE id = :campaign-id;
