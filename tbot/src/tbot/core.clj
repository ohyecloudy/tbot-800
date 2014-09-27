(ns tbot.core
  (:use
   [twitter.oauth]
   [twitter.callbacks]
   [twitter.callbacks.handlers]
   [twitter.api.restful]
   [clojure.java.io])
  (:gen-class))

(defn make-creds [config]
  (make-oauth-creds (config :app-consumer-key)
                    (config :app-consumer-secret)
                    (config :user-access-token)
                    (config :user-access-token-secret)))

(defn load-remain-quote-repo [remain-path]
  (if (.exists (as-file remain-path))
    (read-string (slurp remain-path))
    nil))

(defn load-src-quote-repo [quotes-path master-id]
  (let [shuffled-quotes (shuffle (load-file quotes-path))
        c (count shuffled-quotes)]
    (into [(str "인용구 트윗 한 바퀴 돕니다. 총 인용구는 " c "개입니다. @" master-id)]
          shuffled-quotes)))

(defn quote-repo [quotes-path remain-path master-id]
  (let [remain-repo (load-remain-quote-repo remain-path)
        src-repo-loader #(load-src-quote-repo quotes-path master-id)]
    (if (and (not (nil? remain-repo)) (< 0 (count remain-repo)))
      remain-repo
      (src-repo-loader))))

(defn tweet [creds msg]
  (try
    (statuses-update :oauth-creds creds
                     :params {:status msg})
    (catch Exception e
      (println "caught exception: " (.getMessage e)))))

(defn tweet-then-prepare-next [config]
  (let [src-path (:quotes-path config)
        remain-path (:remain-path config)
        master-id (:master-twitter-id config)
        repo (quote-repo src-path remain-path master-id)]
    (do
      (tweet (make-creds config) (first repo))
      (spit remain-path (prn-str (rest repo))))))

(defn -main [& args]
  (if (not= 1 (count args))
    (println "need config file path ex) ./config.clj")
    (let [configs (load-file (first args))]
      (doall
       (map tweet-then-prepare-next configs)))))

