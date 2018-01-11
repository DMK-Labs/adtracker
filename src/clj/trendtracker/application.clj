(ns trendtracker.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.immutant-web :refer [new-immutant-web]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.postgres :refer [new-postgres-database]]
            [system.components.repl-server :refer [new-repl-server]]
            [system.repl :refer [set-init! start]]
            [trendtracker.config :refer [config]]
            [trendtracker.routes :as routes]))

(defn dev-system
  []
  (component/system-map
   :postgres (new-postgres-database (:db-spec config))
   :middleware (new-middleware {:middleware (:middleware config)})
   :api-routes (new-endpoint routes/api-routes)
   :app-routes (-> (new-endpoint routes/app-routes)
                   (component/using [:middleware]))
   :handler (-> (new-handler)
                (component/using [:api-routes :app-routes]))
   :immutant (-> (new-immutant-web :port (:http-port config))
                 (component/using [:handler]))))

(defn prod-system
  []
  (merge
   (dev-system)
   (component/system-map
    :repl-server (new-repl-server 5602))))

(defn -main
  [& _]
  (set-init! #'prod-system)
  (start))
