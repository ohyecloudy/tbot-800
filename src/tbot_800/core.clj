(ns tbot-800.core
  (:use
    [twitter.oauth]
    [twitter.callbacks]
    [twitter.callbacks.handlers]
    [twitter.api.restful]))

(def my-creds 
  (let [config (load-file "config.clj")]
    (make-oauth-creds (config :app-consumer-key)
                      (config :app-consumer-secret)
                      (config :user-access-token)
                      (config :user-access-token-secret))))

(def quotes (load-file "resources/quotes.clj"))

(defn add-verify-msg [x]
  (let [twit-length-limit 140]
    (->> x
         (map #(if (< (count %) twit-length-limit) 
                 [% nil]
                 [% (str (count %) "로 " twit-length-limit "자를 넘습니다")])))))

(def quotes (add-verify-msg quotes))

; 검증에 실패한 인용구는 로그로 남기고 깨끗한 녀석들만 후보로 올린다.

; (statuses-update :oauth-creds my-creds
;                  :params {:status "Hello, Twitter!"})
