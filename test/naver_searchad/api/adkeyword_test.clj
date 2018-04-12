(ns naver-searchad.api.adkeyword-test
  (:require [clojure.test :refer :all]
            [naver-searchad.api.adkeyword :refer :all]))

(deftest update-keyword-bid!-test
  (let [kw (first (get-by-id (trendtracker.config/creds 137307)
                             ["nkw-a001-01-000001165435118"]))
        old-bid (:bidAmt kw)
        new-bid (+ 10 old-bid)]
    (is (= new-bid
           (get-in (update-keyword-bid!
                    (trendtracker.config/creds 137307)
                    {:keyword-id "nkw-a001-01-000001165435118"
                     :adgroup-id "grp-a001-01-000000006315078"
                     :bid new-bid})
                   [:body :bidAmt])))
    (is (= old-bid
           (get-in (update-keyword-bid!
                    (trendtracker.config/creds 137307)
                    {:keyword-id "nkw-a001-01-000001165435118"
                     :adgroup-id "grp-a001-01-000000006315078"
                     :bid old-bid})
                   [:body :bidAmt])))))



