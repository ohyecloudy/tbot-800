(ns quote-builder-800.core
  (:require [clojure.contrib.command-line :as cmd]
            [clojure.java.io :as io]
            [digest :as d]
            [hiccup.core :as hiccup])
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

(defn append-hash-val [quotes]
  "(\"q1\" \"q2\"...)을 ({:key \"h1\" :quote \"q1\"} ...)로 만든다"
  (map (fn [q] {:key (d/md5 q) :quote q})
       quotes))

(defn build-html [q]
  (hiccup/html [:head
                [:meta {:charset "utf-8"}]
                [:title "인용구"]]
               [:body
                [:p q]]))

(defn build [src-path output-dir]
  (let [flatten-quote (flatten-book-quote (load-file src-path))
        key-quote-pairs (append-hash-val flatten-quote)]
    (do
      (with-open [w (io/writer (str output-dir "/quotes.clj"))]
        (.write w "[")
        (doall (map #(do (.write w (str "\"" % "\""))
                         (.newLine w))
                    flatten-quote))
        (.write w "]"))
      (doall
       (map (fn [p]
              (let [k (:key p)
                    q (:quote p)]
                (with-open [w (io/writer (str output-dir "/" k ".html"))]
                  (.write w (build-html q)))))
            key-quote-pairs)))))

(defn -main [& args]
  (cmd/with-command-line
    args
    "Usage : -i quote-src.clj -o ./output-dir"
    [[i "quoute source file path"]
     [o "build output dir"]]
    (build i o)))
