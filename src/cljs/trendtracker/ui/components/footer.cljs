(ns trendtracker.ui.components.footer
  (:require [antizer.reagent :as ant]
            [keechma.ui-component :as ui]))

(defn render [ctx]
  [ant/layout-footer
   #_[:img {:src "/img/logo/tt-logo-footer.png"}]
   #_[:p>a {:href "http://github.com/sooheon/"
            :style {:color "inherit"}}
      "Github"]
   [:p "DataMKTKorea © 2018 - Made with "
    (rand-nth ["❤️" ;; heart
               "✨" ;; sparkle
               "🌟" ;; glowing star
               ])
    " in Seoul"]])

(def component
  (ui/constructor {:renderer render}))
