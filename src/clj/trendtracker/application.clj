(ns trendtracker.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.aleph :refer [new-web-server]]
            [system.components.middleware :refer [new-middleware]]
            [system.components.postgres :refer [new-postgres-database]]
            [system.components.repl-server :refer [new-repl-server]]
            [system.repl :refer [set-init! start]]
            [trendtracker.config :refer [config]]
            [trendtracker.routes :as routes]
            [trendtracker.modules.scheduler :as scheduler]))

(defn base-system []
  (component/system-map
   :db (new-postgres-database (:db-spec config))
   :middleware (new-middleware {:middleware (:middleware config)})
   :api-middleware (new-middleware {:middleware (:api-middleware config)})
   :api-routes (component/using (new-endpoint routes/api-routes) [:db :api-middleware :middleware])
   :app-routes (component/using (new-endpoint routes/app-routes) [:middleware])
   :handler (component/using (new-handler) [:api-routes :app-routes])
   :http (component/using (new-web-server (:http-port config)) [:handler])))

(defn prod-system []
  (merge
   (base-system)
   (component/system-map
    :repl-server (new-repl-server 5602)
    :schedule (scheduler/new-scheduler))))

(defn -main
  [& _]
  (set-init! #'prod-system)
  (start))
