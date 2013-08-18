(ns tbot-800.core
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful])
  (:import 
   (java.util TimerTask Timer)))

(def my-creds 
  (let [config (load-file "config.clj")]
    (make-oauth-creds (config :app-consumer-key)
                      (config :app-consumer-secret)
                      (config :user-access-token)
                      (config :user-access-token-secret))))

(def tweet-time-interval
  (:tweet-interval-ms (load-file "config.clj")))

(def quotes
  (filter (fn [q] (let [twit-length-limit 140]
                    (<= (count q) twit-length-limit)))
          (load-file "resources/quotes.clj")))

(defn tweet [msg]
  (do
   (println "tweet - " msg)
   (try
     (statuses-update :oauth-creds my-creds
                    :params {:status msg})
     (catch Exception e
       (println "caught exception: "
                (.getMessage e))))))

(defn register-schedule-tweet [interval]
  (let [task (proxy [TimerTask] []
               (run []
                 (tweet (rand-nth quotes))))
        delay (long 1000)]
    (. (new Timer) (schedule task delay (long interval)))))

(defn -main [& args]
  (register-schedule-tweet tweet-time-interval))
