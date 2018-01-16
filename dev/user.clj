(ns user
  (:require [clojure.core.match :refer [match]]
            [system.repl :refer [reset set-init! start stop system]]
            [trendtracker.application :refer [dev-system]]
            [trendtracker.db :as db]))

(set-init! #'dev-system)

(let [in [:powerlink :cmp-1 :dsfds]]
  (match in
    [:total] [:total]
    [type] [type]
    [type cmp-id] [:campaign cmp-id]
    [type cmp-id adgrp-id] [:adgroup adgrp-id]))
