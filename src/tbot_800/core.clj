(ns tbot-800.core
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]
   [clojure.java.io]
   [environ.core])
  (:require [clojure.tools.logging :as log])
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

(defmacro forever [& body]
  `(while true ~@body))

(defn set-interval [callback ms]
  (future (forever (do (Thread/sleep ms) (callback)))))

(defn tweet-builder [config]
  (let [master-id (:master-twitter-id config)
        src-url (:quotes-url config)
        quote-builder (partial build-quotes master-id)]
    (fn [] (tweet (make-creds config) (pop-quote quote-builder src-url)))))

(defn register-tweet-scheduler [config]
  (let [interval (* (Integer/parseInt (:tweet-interval-min config)) 60 1000)]
    (do
      (log/info "register tweet scheduler")
      (set-interval (tweet-builder config) interval))))

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
          (map #(vector (keyword %) (env (keyword (str % "-" num)))) template)))

(defn load-configs [env]
  (let [loader (partial load-config-elem config-unit env)]
    (filter #(not-any? nil? (vals %)) (map loader (range 0 10)))))

(defn wait-tweet-scheduler [coll]
  (letfn [(end-cond [coll] (every? future-done? coll))]
    (while (end-cond coll))))

(defn -main [& args]
  (let [configs (load-configs env)]
    (if (empty? configs)
      (do
        (println "set environment variables")
        (println (map #(str (clojure.string/replace % #"-" "_") "_1")
                     config-unit)))
      (wait-tweet-scheduler (map register-tweet-scheduler configs)))))
