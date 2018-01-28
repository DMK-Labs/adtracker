-- :name user :? :1
-- :doc Selects user
SELECT
  u.id,
  u.email::TEXT,
  u.name,
  t.name AS tenant,
  t.naver_id AS "naver-id"
FROM trendtracker.user AS u
  LEFT JOIN trendtracker.tenant AS t
    ON u.tenant_id = t.id
WHERE email = :email AND
      password = crypt(:password, password);

-- :name access-rights :? :*
-- :doc Returns managed clients belonging to client
select * from naver.access_rights;
