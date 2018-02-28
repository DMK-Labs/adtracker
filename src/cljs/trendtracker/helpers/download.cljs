(ns trendtracker.helpers.download
  (:require [testdouble.cljs.csv :as csv]
            cljsjs.filesaverjs
            [semantic-csv.core :as scsv]))

(defn download-csv
  [{:keys [filename header content prepend-header]}]
  (let [mime-type (str "text/plain;charset=" (.-characterSet js/document))
        csv (->> content
                 (scsv/vectorize {:header header
                                  :prepend-header prepend-header})
                 csv/write-csv)
        blob (new js/Blob
                  (clj->js [csv])
                  (clj->js {:type mime-type}))]
    (js/saveAs blob filename)))
