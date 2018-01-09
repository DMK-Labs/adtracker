(ns trendtracker.test-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [trendtracker.core-test]
   [trendtracker.common-test]))

(enable-console-print!)

(doo-tests 'trendtracker.core-test
           'trendtracker.common-test)
