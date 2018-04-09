(ns trendtracker.modules.sync-test
  (:require [clojure.test :refer :all]
            [trendtracker.modules.sync :refer [pull-unpulled-recent-stats
                                               download-stats]]
            [trendtracker.config :refer [config]]))

(deftest pull-unpulled-recent-stats-test
  #_(pull-unpulled-recent-stats (:db-spec config) "AD"))

(deftest download-stats-test)

