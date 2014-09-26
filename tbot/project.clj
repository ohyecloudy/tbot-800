(defproject tbot "0.2.0-SNAPSHOT"
  :description "twitter bot"
  :url "https://github.com/ohyecloudy/tbot-800"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [twitter-api "0.7.5"]
                 [clj-time "0.6.0"]]
  :plugins [[cider/cider-nrepl "0.7.0-SNAPSHOT"]]
  :main tbot.core)
