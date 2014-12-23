[![Build Status](https://travis-ci.org/ohyecloudy/tbot-800.svg?branch=master)](https://travis-ci.org/ohyecloudy/tbot-800)

# tbot-800

일정한 시간 간격으로 `tweet`하는 봇.

## example

```
$ lein uberjar
$ export app_consumer_keyN=FIXME
$ export app_consumer_secretN=FIXME
$ export user_access_tokenN=FIXME
$ export user_access_token_secretN=FIXME
$ export quotes_urlN=FIXME
$ export master_twitter_idN=FIXME
$ export tweet_interval_minN=1
$ java -jar target/tbot-800-standalone.jar
```

* N = [0, 9]
* 트위터 계정 여러개를 정의할 수 있습니다.

## 운영 중인 트위터 봇

* [@book\_quote\_bot](https://twitter.com/book_quote_bot)
* [@bquote\_bot](https://twitter.com/bquote_bot)

## read more about tbot-800

* [tbot-800 프로젝트 개발 일기](http://ohyecloudy.com/ddiary/categories.html#tbot-800-ref)
* [클로저(clojure)로 짠 트위터 봇 tbot-800을 출동시키고](http://ohyecloudy.com/pnotes/archives/1850)

## license

Copyright © 2013 Oh Jongbin

Distributed under the Eclipse Public License, the same as Clojure.
