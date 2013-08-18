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

(def quotes
  (filter (fn [q] (let [twit-length-limit 140]
                    (<= (count q) twit-length-limit)))
          (load-file "resources/quotes.clj")))

(defn tweet-random-quote []
  (statuses-update :oauth-creds my-creds
                   :params {:status (rand-nth quotes)}))

