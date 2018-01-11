(ns user
  (:require [trendtracker.application :refer [dev-system]]
            [system.repl :refer [system set-init! start stop reset]]))

(set-init! #'dev-system)
