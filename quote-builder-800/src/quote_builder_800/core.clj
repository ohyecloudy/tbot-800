(ns quote-builder-800.core
  (:require [clojure.contrib.command-line :as cmd]
            [clojure.java.io :as io])
  (:gen-class))

(defn flatten-book-quote [q]
  "[{:source src :quotes [q1, ...]} ...]를
   (\"q1 {src}\" \"q2 {src}\"...)로 인용구를 만든다"
  (mapcat
   (fn [quote-group]
     (let [postfix
           (when (:source quote-group)
             (str " {" (:source quote-group) "}"))]
       (map #(str % postfix) (:quotes quote-group))))
   q))

(defn build [src-path output-dir]
  (let [flatten-quote (flatten-book-quote (load-file src-path))]
    (with-open [w (io/writer (str output-dir "/quotes.clj"))]
      (.write w "[")
      (doall (map #(do (.write w (str "\"" % "\""))
                       (.newLine w))
                  flatten-quote))
      (.write w "]"))))

(defn -main [& args]
  (cmd/with-command-line
    args
    "Usage : -i quote-src.clj -o ./output-dir"
    [[i "quoute source file path"]
     [o "build output dir"]]
    (build i o)))
