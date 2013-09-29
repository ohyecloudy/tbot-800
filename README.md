# tbot-800

일정한 시간 간격으로 `tweet`을 하는 봇. `인용구` 리스트를 만드는 `quote builder 800`과 `tweet`을 하는 `tbot`으로 구성.

    "Usage : -i quote-src.clj -o ./output-dir"

## example

운영 중인 트위터 봇

* [@book\_quote\_bot](https://twitter.com/book_quote_bot)

## getting start

```
$ git clone https://github.com/ohyecloudy/tbot-800.git tbot-800
```

[Clojure](http://clojure.org/)로 만들었으며 [Leiningen](http://leiningen.org/)이 필요하다.

### quote builder 800

```
cd tbot-800/quote-builder-800
lein repl
```

``` clojure
quote-builder-800.core=> (build "quote-src.clj" "./output-dir")
```

## License

Copyright © 2013 Oh Jongbin

Distributed under the Eclipse Public License, the same as Clojure.

