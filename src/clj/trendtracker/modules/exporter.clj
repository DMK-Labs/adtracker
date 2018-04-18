(ns trendtracker.modules.exporter
  (:require [dk.ative.docjure.spreadsheet :as excel]
            [semantic-csv.core :as scsv]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(defn sheet-contents [keyword-bids-rel]
  (vec
   (concat
    [["Find an ad group ID and keyword ID in Ad Download file."]
     ["The request for keyword edit will be taken based on the keyword ID. Enter the bid amount without a comma or KRW, and with only numbers."]
     ["A keyword bid will be saved as the bid amount. Even if there is a default bid, the ad is running at the keyword bid."]
     ["You can enter up to 10,000 data."]
     ["[Important] Input values from the line 7 will be reflected to the system. Do not delete lines from 1 to 6. While saving in Excel, necessarily check the file format of CSV."]
     ["Ad group ID" "Keyword ID (required)" "Keyword" "Keyword bid (numbers only)"]]
    (scsv/vectorize {:header [:adgroup-id :keyword-id :keyword :bid]
                     :prepend-header false}
                    keyword-bids-rel))))

(defn write-bulk-bid-sheet [filename final-bids-rel]
  (let [wb (excel/create-workbook
            "en_edit_keyword_bid_template"
            (sheet-contents final-bids-rel))]
    (excel/save-workbook! filename wb)))


