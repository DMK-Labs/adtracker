(ns trendtracker.application
  (:gen-class)
  (:require [com.stuartsierra.component :as component]
            [system.components.aleph :refer [new-web-server]]
            [system.components.endpoint :refer [new-endpoint]]
            [system.components.handler :refer [new-handler]]
            [system.components.middleware :refer [new-middleware]]
            [trendtracker.config :refer [config]]
            [trendtracker.routes :as routes]))

(defn app-system [config]
  (component/system-map
   :middleware (new-middleware {:middleware (:middleware config)})
   :api-routes (new-endpoint routes/api-routes)
   :app-routes (-> (new-endpoint routes/app-routes)
                   (component/using [:middleware]))
   :handler (-> (new-handler)
                (component/using [:api-routes :app-routes]))
   :http (-> (new-web-server (:http-port config))
             (component/using [:handler]))))

(defn -main [& _]
  (let [config (config)]
    (-> config
        app-system
        component/start)
    (println "Started trendtracker on" (str "http://localhost:" (:http-port config)))))
