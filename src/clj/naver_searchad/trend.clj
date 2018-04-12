(ns naver-searchad.trend
  (:require [aleph.http :as http]
            [clojure.string :as str]
            [jsonista.core :as json]
            ))

(def login-url "https://searchad.naver.com/auth/login")


(defn body [resp]
  (-> resp
      deref
      :body
      (json/read-value (json/object-mapper {:decode-key-fn true}))))

(defn token
  [login-id password]
  (let [resp (http/post
              login-url
              {:headers {"Content-Type" "application/json"
                         "Referer" "https://searchad.naver.com"}
               ;; :form-params form-params
               :body (format "{\"loginId\":\"%s\",\"loginPwd\":\"%s\"}"
                             login-id
                             password)})
        {:keys [token]} (body resp)]
    token))

(defn trend [token keyword]
  (->> {:headers {"authorization" (str "Bearer " token)}}
       (http/get
        (str "https://manage.searchad.naver.com/keywordstool?format=json&siteId=&mobileSiteId=&hintKeywords="
             keyword
             "&includeHintKeywords=0&showDetail=1&biztpId=&mobileBiztpId=&month=&event=&keyword="
             keyword))
       body
       :keywordList
       first))

(defn frends
  [token keywords]
  (reduce
   (fn [res kws]
     (Thread/sleep 250)
     (->> {:headers {"authorization"
                     (str "Bearer " token)}}
          (http/get
           (str
            "https://manage.searchad.naver.com/keywordstool?format=json&siteId=&mobileSiteId=&hintKeywords="
            (str/join "," kws)
            "&includeHintKeywords=0&showDetail=1&biztpId=&mobileBiztpId=&month=&event=&keyword="))
          body
          :keywordList
          (into res)))
   #{}
   (partition-all 5 keywords)))

