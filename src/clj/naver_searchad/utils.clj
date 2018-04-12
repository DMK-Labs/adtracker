(ns naver-searchad.utils
  (:require [camel-snake-kebab.core :as csk]
            [camel-snake-kebab.extras :as csk-extras]))

(def ->camel-keys
  #(csk-extras/transform-keys csk/->camelCase %))
