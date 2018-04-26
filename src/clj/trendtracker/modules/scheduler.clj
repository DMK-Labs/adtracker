(ns trendtracker.modules.scheduler
  (:require [com.stuartsierra.component :as component]
            [tea-time.core :as tt]
            [tick.core :as t]
            [taoensso.timbre :as timbre]
            [trendtracker.modules.sync :as sync])
  (:import (java.time Duration Instant)))

(defn six-am-tomorrow []
  (-> (t/tomorrow)
      (t/at (t/time 6))
      (t/at-zone (t/zone "+9"))
      (t/instant)))

(defn seconds-until [time]
  (.getSeconds
   ^Duration (Duration/between
              (Instant/now)
              time)))

(defn full-sync [clients]
  (do
    (doseq [client clients]
      (sync/sync-client-masters client ["Campaign" "BusinessChannel" "Adgroup" "Ad"]) ;; TODO: also sync keywords, ads
      (sync/sync-keywords client)
      (sync/pull-and-append client :ad)
      (sync/pull-and-append client :ad-conversion))
    (sync/refresh-keyword-stats-view)))

(defn task []
  (tt/every! (* 60 60 24)
             (seconds-until (six-am-tomorrow))
             (bound-fn []
               (timbre/info "Running scheduled sync")
               (full-sync [137307 719425 777309]))))

(defrecord Scheduler [schedule]
  component/Lifecycle
  (start [component]
    (timbre/info "Starting scheduler")
    (tt/start!)
    (let [sched (task)]
      (assoc component :schedule sched)))

  (stop [component]
    (timbre/info "Stopping scheduler")
    (tt/cancel! schedule)
    (assoc component :schedule nil)))

(defn new-scheduler []
  (map->Scheduler {}))
