(defproject quote-builder-800 "0.1.0-SNAPSHOT"
  :description "quote builder"
  :url "https://github.com/ohyecloudy/tbot-800"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/tools.cli "0.3.1"]
                 [digest "1.4.3"]
                 [hiccup "1.0.4"]]
  :plugins [[cider/cider-nrepl "0.7.0-SNAPSHOT"]]
  :uberjar-name "quote-builder-800-standalone.jar"
  :main quote-builder-800.core)
