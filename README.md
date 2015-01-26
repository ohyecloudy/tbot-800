[![Build Status](https://travis-ci.org/ohyecloudy/tbot-800.svg?branch=master)](https://travis-ci.org/ohyecloudy/tbot-800)

# tbot-800

일정한 시간 간격으로 `tweet`하는 봇.

## example

```
$ export app_consumer_key_0=FIXME
$ export app_consumer_secret_0=FIXME
$ export user_access_token_0=FIXME
$ export user_access_token_secret_0=FIXME
$ export quotes_url_0=FIXME
$ export master_twitter_id_0=FIXME
$ export tweet_interval_min_0=1
$ lein trampoline run
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
