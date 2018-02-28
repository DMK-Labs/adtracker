(ns trendtracker.core
  (:require [huri.core :as h]
            [optimus.mckp :as mckp]
            [trendtracker.config :refer [config]]
            [trendtracker.models.keywords :as keywords]
            [trendtracker.modules.landscape :as landscape]
            [trendtracker.models.optimize :as optimize]))

(defonce sys-df (atom #{}))
(defonce ca-df (atom #{}))

(comment
  (def x (future (landscape/add-portfolio-estimates sys-df 1334028)))
  (count (landscape/added sys-df :mobile))
  (count (landscape/added sys-df :pc))
  ;; (optimize/insert-estimates 1334028 @sys-df)
  (optimize/insert-click-marginals
   1334028
   (mckp/marginal-landscape
    :clicks
    (h/where {:bid [< (:bid-limit (optimize/settings 1334028))]
              :key (set (map :nccKeywordId (keywords/eligible 1334028)))}
             @sys-df))))

(comment
  (def y (future (landscape/add-portfolio-estimates ca-df 137307)))
  (count (landscape/added ca-df :mobile))
  (count (landscape/added ca-df :pc))
  (optimize/insert-click-marginals
   137307
   (mckp/marginal-landscape
    :clicks
    (h/where {:bid [< (:bid-limit (optimize/settings 137307))]
              :key (set (map :nccKeywordId (keywords/eligible 137307)))}
             @ca-df))))
