(ns trendtracker.modules.output
  (:require [dk.ative.docjure.spreadsheet :as excel]
            [semantic-csv.core :as scsv]))

(defn- sheet-contents [final-bids-rel]
  (concat
   [["Find an ad group ID and keyword ID in Ad Download file."]
    ["The request for keyword edit will be taken based on the keyword ID. Enter the bid amount without a comma or KRW, and with only numbers."]
    ["A keyword bid will be saved as the bid amount. Even if there is a default bid, the ad is running at the keyword bid."]
    ["You can enter up to 10,000 data."]
    ["[Important] Input values from the line 7 will be reflected to the system. Do not delete lines from 1 to 6. While saving in Excel, necessarily check the file format of CSV."]
    ["Ad group ID" "Keyword ID (required)" "Keyword" "Keyword bid (numbers only)"]]
   (->> final-bids-rel
        (scsv/vectorize {:header [:adgroup-id :key :keyword :bid]})
        rest)))

(defn write-bulk-bid-sheet [title final-bids-rel]
  (let [wb (excel/create-workbook
            "en_edit_keyword_bid_template"
            (sheet-contents final-bids-rel))]
    (excel/save-workbook! title wb)))
