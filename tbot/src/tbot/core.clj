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

(defn make-creds [config]
  (make-oauth-creds (config :app-consumer-key)
                    (config :app-consumer-secret)
                    (config :user-access-token)
                    (config :user-access-token-secret)))
(def quotes (ref nil))

(defn init-quotes [quotes-path]
  (let [shuffled-quotes (shuffle (load-file quotes-path))
        c (count shuffled-quotes)]
    (into [(str "인용구 트윗 한 바퀴 돕니다. 총 인용구는 " c "개입니다.")] shuffled-quotes)))

(defn next-quote [quotes-path]
  (do
    (when (empty? (deref quotes))
      (dosync (ref-set quotes (init-quotes quotes-path))))
    (let [q (first (deref quotes))]
      (dosync (ref-set quotes
                       (rest (deref quotes))))
      q)))

(defn tweet [msg creds]
  (do
    (write-log (str "tweet - " msg))
    (try
      (statuses-update :oauth-creds creds
                       :params {:status msg})
      (catch Exception e
        (println "caught exception: "
                 (write-log (.getMessage e)))))))

(defn register-schedule-tweet [interval creds quotes-path]
  (let [task (proxy [TimerTask] []
               (run []
                 (tweet (next-quote quotes-path) creds)))
        delay (long 1000)]
    (. (new Timer) (schedule task delay (long interval)))))

(defn -main [& args]
  (if (not= 1 (count args))
    (println "need config file path ex) ./config.clj")
    (let [config (load-file (first args))]
      (register-schedule-tweet (:tweet-interval-ms config)
                               (make-creds config)
                               (:quotes-path config)))))

