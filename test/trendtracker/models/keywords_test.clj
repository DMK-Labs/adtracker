(ns trendtracker.models.keywords-test
  (:require [clojure.test :refer :all])
  (:require [trendtracker.models.keywords :refer :all]))

(deftest eligible-test
  (= (set (eligible-dep 137307)) (set (eligible 137307))))

(comment
  (time (eligible-dep 137307))
  (time (eligible 137307)))
