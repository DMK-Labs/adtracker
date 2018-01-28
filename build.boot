(def project 'trendtracker)
(def version "0.1.0-SNAPSHOT")

(set-env!
 :source-paths #{"src/clj" "src/cljs" "src/cljc" "src/less"}
 :resource-paths #{"resources"}
 :dependencies '[[org.clojure/clojure "1.9.0"]
                 [org.clojure/clojurescript "1.9.946"]
                 [org.clojure/core.match "0.3.0-alpha5"]

                 ;; boot tasks
                 [adzerk/boot-cljs "2.1.4" :scope "test"]
                 [powerlaces/boot-figreload "0.5.14" :scope "test"]
                 [deraen/boot-less "0.6.2" :scope "test"]
                 [org.clojure/tools.namespace "0.3.0-alpha4" :scope "test"]
                 [onetom/boot-lein-generate "0.1.3" :scope "test"]
                 [adzerk/boot-cljs-repl "0.3.3" :scope "test"]
                 [com.cemerick/piggieback "0.2.2" :scope "test"]
                 [weasel "0.7.0" :scope "test"]
                 [org.clojure/tools.nrepl "0.2.13" :scope "test"]

                 ;; frontend
                 [metosin/compojure-api "2.0.0-alpha17"]
                 [reagent "0.7.0"]
                 [keechma "0.3.1" :exclusions [cljsjs/react-with-addons]]
                 [keechma/toolbox "0.1.7"]
                 [keechma/forms "0.1.3"]
                 [sooheon/antizer "0.2.4-SNAPSHOT"]
                 [reacharts "0.1.0-SNAPSHOT"]
                 [hodgepodge "0.1.3"]

                 ;; backend
                 [org.immutant/web "2.1.10"]
                 [org.clojure/java.jdbc "0.7.5"]
                 [org.postgresql/postgresql "42.2.0"]
                 [com.layerware/hugsql "0.4.8"]
                 [ring-middleware-format "0.7.2"]
                 [clojure.java-time "0.3.1"]
                 [naver-searchad "0.1.0-SNAPSHOT"]
                 [com.taoensso/nippy "2.14.0"]
                 [buddy "2.0.0"]

                 ;; data munging
                 [huri "0.10.0-SNAPSHOT"]
                 [json-html "0.4.4"]

                 ;; added by chestnut
                 [com.cognitect/transit-clj "0.8.300"]
                 [ring "1.6.3"]
                 [ring/ring-defaults "0.3.1"]
                 [bk/ring-gzip "0.2.1"]
                 [compojure "1.6.0"]
                 [environ "1.1.0"]
                 [com.stuartsierra/component "0.3.2"]
                 [org.danielsz/system "0.4.1"]])

(require '[adzerk.boot-cljs :refer [cljs]]
         '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl cljs-repl-env]]
         '[powerlaces.boot-figreload :refer [reload]]
         '[deraen.boot-less :refer [less]]
         '[com.stuartsierra.component :as component]
         '[system.boot :refer [system]]
         'clojure.tools.namespace.repl
         'boot.lein)

(boot.lein/generate)

(task-options!
 aot {:all true}
 pom {:project     project
      :version     version
      :description ""
      :url         "http://trendtracker.co.kr"
      :scm         {:url ""}
      :license     {}}
 jar {:main 'trendtracker.application
      :file (str project ".jar")})

(def repl-port 5601)

(deftask dev
  "This is the main development entry point."
  []
  ;; Needed by tools.namespace to know where the source files are
  (apply clojure.tools.namespace.repl/set-refresh-dirs (get-env :directories))
  (require 'trendtracker.application)
  (comp
   (watch)
   (system :sys (resolve 'trendtracker.application/dev-system)
           :auto true
           :files ["routes.clj" "application.clj"])
   (less)
   (reload)
   ;; this is also the server repl!
   (cljs-repl :nrepl-opts {:client false
                           :port repl-port
                           ;; :init-ns 'user
                           })
   (cljs :optimizations :none)
   (target)))

(deftask static
  "This is used for creating optimized static resources under static"
  []
  (comp
   (less :compression true)
   (cljs :optimizations :advanced)))

(deftask uberjar
  "Build an uberjar"
  []
  (println "Building uberjar")
  (comp
   (static)
   (aot)
   (pom)
   (uber)
   (jar)
   (target :no-clean true)))
