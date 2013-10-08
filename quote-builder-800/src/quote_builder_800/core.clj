(ns quote-builder-800.core
  (:require [clojure.contrib.command-line :as cmd]
            [clojure.java.io :as io]
            [digest :as d]
            [hiccup.page :as hp])
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

(def tweet-quote-str-count 100)

(defn build-html [q twitter-card-creator]
; 트위터 카드에는 트윗하는 인용구 이후를 붙여서 트위터 카드로 최대한 볼 수 있게 한다.
  (let [twitter-card-desc (if (> (count q) tweet-quote-str-count)
                            (apply str (drop tweet-quote-str-count q))
                            q)]
    (hp/html5 {:lang "en"}
              [:head
               [:meta {:charset "utf-8"}]
               [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
               [:meta {:name "twitter:card" :content "summary"}]
               [:meta {:name "twitter:title" :content "카드로도 다 못 보면 링크 고고"}]
               [:meta {:name "twitter:creator" :content twitter-card-creator}]
               [:meta {:name "twitter:description" :content twitter-card-desc}]
               [:link {:href "http://netdna.bootstrapcdn.com/bootstrap/3.0.0/css/bootstrap.min.css" :rel "stylesheet"}]
               [:title "인용구"]]
              [:body
               [:div.container
                [:div.row
                 [:div {:class "col-md-6 col-md-offset-3"}
                  [:div.page-header
                   [:h1 "인용구 "
                    [:small
                     [:a {:href (str "http://twitter.com/" twitter-card-creator)
                          :target "blank"} twitter-card-creator]]]]]]
                [:div.row
                 [:div {:class "col-md-6 col-md-offset-3"}
                  [:p.lead q]]]]])))

(defn adjust-quote [quote url]
  (if (<= (count quote) 140)
    quote
    (str (apply str (take tweet-quote-str-count quote)) "... " url)))

(defn write-quotes [output-dir key-quote-pairs base-url]
  (with-open [w (io/writer (str output-dir "/quotes.clj"))]
    (.write w "[")
    (doall (map (fn [p]
                  (let [url (str base-url (:key p) ".html")
                        q (adjust-quote (:quote p) url)]
                    (do (.write w (str "\"" q "\""))
                        (.newLine w))))
                key-quote-pairs))
    (.write w "]")))

(defn build [src-path output-dir base-url twitter-card-creator]
  (let [key-quote-pairs (append-hash-val (flatten-book-quote (load-file src-path)))]
    (do
      (write-quotes output-dir key-quote-pairs base-url)
      (doall
       (map (fn [p]
              (let [k (:key p)
                    q (:quote p)]
                (with-open [w (io/writer (str output-dir "/" k ".html"))]
                  (.write w (build-html q twitter-card-creator)))))
            key-quote-pairs)))))
