(ns tbot-800.core
  (:use
    [twitter.oauth]
    [twitter.callbacks]
    [twitter.callbacks.handlers]
    [twitter.api.restful]))

(defn load-config [n]
  (with-open [r (clojure.java.io/reader n)]
    (read (java.io.PushbackReader. r))))

(def my-creds 
  (let [config (load-config "config.clj")]
    (make-oauth-creds (config :app-consumer-key)
                      (config :app-consumer-secret)
                      (config :user-access-token)
                      (config :user-access-token-secret))))

(statuses-update :oauth-creds my-creds
                 :params {:status "Hello, Twitter!"})

