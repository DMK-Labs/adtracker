(ns user
  (:require [system.repl :refer [system set-init! start stop reset]]
            [trendtracker.application :refer [base-system]]
            [figwheel-sidecar.system :as fw-sys]
            [figwheel-sidecar.config :as fw-config]))

(defn dev-system []
  (assoc (#'base-system)
         :figwheel-system (fw-sys/figwheel-system (fw-config/fetch-config))
         :css-watcher (fw-sys/css-watcher {:watch-paths ["resources/public/css"]})))

(set-init! #'dev-system)

(defn cljs-repl []
  (fw-sys/cljs-repl (:figwheel-system system)))

