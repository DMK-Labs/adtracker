(ns trendtracker.options
  (:require [goog.string :as gstring]
            goog.string.format))

(def pagination
  {:size :small
   :hideOnSinglePage true
   :showTotal (fn [total [start end]]
                (gstring/format "총 %s개 중 %s-%s" total start end))
   :defaultPageSize 10
   :pageSizeOptions ["10" "20" "50"]
   :showSizeChanger true})
