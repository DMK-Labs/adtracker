(ns trendtracker.components.shell-component
  (:require [clojure.java.shell :as shell]
            [clojure.string :as str]
            [com.stuartsierra.component :as component]))

(defrecord ShellComponent [command]
  component/Lifecycle
  (start [this]
    (when-not (:running this)
      (println "Shell command:" (str/join " " command))
      (future (apply shell/sh command)))
    (assoc this :running true)))

(defn shell-component [& cmd]
  (->ShellComponent cmd))
