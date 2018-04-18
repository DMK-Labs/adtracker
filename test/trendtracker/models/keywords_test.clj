(ns trendtracker.models.keywords-test
  (:require [clojure.test :refer :all]
            [trendtracker.models.keywords :refer :all]
            [trendtracker.config :refer [config creds]]))

(comment
  (time (eligible-dep 137307))
  (time (eligible 137307))
  (by-customer (:db-spec config) {:low "2018-02-01" :high "2018-02-31" :customer 137307}))
