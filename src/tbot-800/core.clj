(ns tbot-800.core
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]
   [clojure.java.io]
   [environ.core])
  (:require [clojure.tools.logging :as log])
  (:import
   (java.util TimerTask Timer))
  (:gen-class))

(defn make-creds [config]
  (make-oauth-creds (config :app-consumer-key)
                    (config :app-consumer-secret)
                    (config :user-access-token)
                    (config :user-access-token-secret)))

(defn build-quotes [master-id url]
  (let [shuffled-quotes (shuffle (read-string (slurp url)))
        c (count shuffled-quotes)]
    (into [(str "인용구 트윗 한 바퀴 돕니다. 총 인용구는 " c "개입니다. @" master-id)]
          shuffled-quotes)))

(def quotes (ref {}))
(defn pop-quote [builder url]
  (let [k (keyword url)]
    (do
      (when (empty? (k @quotes))
        (dosync (ref-set quotes (assoc @quotes k (builder url)))))
      (let [q (first (k @quotes))]
        (dosync (ref-set quotes (assoc @quotes k (rest (k @quotes)))))
        q))))

(defn tweet [creds msg]
  (try
    (log/info "tweet : " msg)
    (statuses-update :oauth-creds creds
                     :params {:status msg})
    (catch Exception e
      (log/error "caught exception: " (.getMessage e)))))

(defn register-tweet-scheduler [config]
  (let [master-id (:master-twitter-id config)
        src-url (:quotes-url config)
        quote-builder (partial build-quotes master-id)
        interval (* (:tweet-interval-min config) 60 1000)
        task (proxy [TimerTask] []
               (run []
                 (tweet (make-creds config) (pop-quote quote-builder src-url))))
        delay (long 1000)]
    (. (new Timer) (schedule task delay (long interval)))))

(def config-unit
  ["app-consumer-key"
   "app-consumer-secret"
   "user-access-token"
   "user-access-token-secret"
   "quotes-url"
   "master-twitter-id"
   "tweet-interval-min"])


(defn load-config-elem [template env num]
  (reduce conj {}
          (map #(vector % (env (str % "-" num))) template)))

(defn load-configs [env]
  (let [loader (partial load-config-elem config-unit env)]
    (filter #(not-any? nil? (vals %)) (map loader (range 0 10)))))

(defn -main [& args]
  (let [configs (load-configs env)]
    (if (empty? configs)
      (log/error (str "set "
                      (clojure.string/join ", "
                                           (map #(str % "[N]") config-unit))))
      (doall
       (map register-tweet-scheduler configs)))))
