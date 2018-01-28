-- resources/sql/queries.sql
-- Adgroup/Campaign/Type Performance Queries

-- :name adgrp-perf-by-id-date :? :*
-- :doc Selects daily adgroup perfs by date and id filters.
select * from naver.adgroup_performance
where customer_id = :customer-id
and ad_group_id = :id
and during between :low::date and :high::date
order by during asc

-- :name cmp-perf-by-id-date :? :*
-- :doc Selects daily campaign perfs by date and id filters.
select * from naver.campaign_performance
where customer_id = :customer-id
and campaign_id = :id
and during between :low::date and :high::date
order by during asc

-- :name cmp-type-perf :? :*
-- :doc Selects daily campaign-type perfs by date and name filters.
select * from naver.campaign_type_performance
where customer_id = :customer-id
and campaign_type = :type
and during between :low::date and :high::date
order by during asc

-- :name total-perf-by-date :? :*
-- :doc Selects daily total perfs by date.
select * from naver.total_performance
where customer_id = :customer-id
and during between :low::date and :high::date
order by during asc

-- :name send-event* :! :n
-- :doc Write an event to event.log
insert into event.log (type, data)
values (:type, :data)

-- :name events* :? :*
-- :doc Selects events
select * from event.log
where status ISNULL
and type = :type
