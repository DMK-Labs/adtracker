-- resources/sql/queries.sql
-- Adgroup/Campaign/Type Performance Queries

-- :name adgrp-perf-by-id-date :? :*
-- :doc Selects daily adgroup perfs by date and id filters.
select * from adgroup_performance
where customer_id = :customer-id
and ad_group_id = :id
and during between :low::date and :high::date


-- :name cmp-perf-by-id-date :? :*
-- :doc Selects daily campaign perfs by date and id filters.
select * from campaign_performance
where customer_id = :customer-id
and campaign_id = :id
and during between :low::date and :high::date

-- :name total-perf-by-date :? :*
-- :doc Selects daily total perfs by date.
select * from total_performance
where customer_id = :customer-id
and during between :low::date and :high::date
