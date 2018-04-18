(ns trendtracker.models.daily-stats-test
  (:require [clojure.test :refer :all]
            [trendtracker.models.daily-stats :refer :all]))

(comment
 (map add-ratios2
      (by-customer
       (:db-spec config)
       {:customer-id 137307}))
 (by-adgroup
  (:db-spec config)
  {:adgroup-id "grp-a001-01-000000005994190"
   :low "2018-02-21"
   :high "2018-02-27"
   :customer-id 137307})
 (by-campaign
  (:db-spec config)
  {:campaign-id "cmp-a001-01-000000001015651"
   :low "2018-02-21"
   :high "2018-02-27"
   :customer-id 137307})
 (by-type
  {:campaign-type "powerlink"
   :low "2018-02-21" :high "2018-02-27"
   :customer-id 137307}))

