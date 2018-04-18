(ns trendtracker.modules.exporter-test
  (:require [clojure.test :refer :all]
            [trendtracker.modules.exporter :refer :all]
            [trendtracker.models.bid-recommender :refer :all]
            [trendtracker.config :refer [config]]
            [huri.core :as h]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]))

(comment
 (write-bulk-bid-sheet "bids.xlsx" [])

 (with-open [w (io/writer "out-test-file.csv")]
   (csv/write-csv
    w
    (sheet-contents
     (->>
       (h/where {:profit neg? :clicks [> 5]})
       (map #(assoc % :bid 70)))))))
