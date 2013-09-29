(ns tbot.core
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]
   [clojure.java.io]
   [clj-time.local]
   [clj-time.format])
  (:import 
   (java.util TimerTask Timer))
  (:gen-class))

(defn write-log [msg]
  (letfn [(logFileName []
            (str (unparse (formatter "yyyy-MM-dd") (local-now))
                 ".log.txt"))
          (timestamp []
            (str (local-now)))]
    (with-open [wrtr (writer (logFileName) :append true)]
      (do
        (.write wrtr (timestamp))
        (.newLine wrtr)
        (.write wrtr msg)
        (.newLine wrtr)))))

(def my-creds 
  (let [config (load-file "config.clj")]
    (make-oauth-creds (config :app-consumer-key)
                      (config :app-consumer-secret)
                      (config :user-access-token)
                      (config :user-access-token-secret))))
(def tweet-time-interval
  (:tweet-interval-ms (load-file "config.clj")))
(def quotes-path
  (:quotes-path (load-file "config.clj")))
(def quotes
  (filter (fn [q] (let [twit-length-limit 140]
                    (<= (count q) twit-length-limit)))
          (load-file quotes-path)))

(defn tweet [msg]
  (do
    (write-log (str "tweet - " msg))
    (try
      (statuses-update :oauth-creds my-creds
                       :params {:status msg})
      (catch Exception e
        (println "caught exception: "
                 (write-log (.getMessage e)))))))

(defn register-schedule-tweet [interval]
  (let [task (proxy [TimerTask] []
               (run []
                 (tweet (rand-nth quotes))))
        delay (long 1000)]
    (. (new Timer) (schedule task delay (long interval)))))

(defn -main [& args]
  (register-schedule-tweet tweet-time-interval))
