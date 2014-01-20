#!/bin/bash

MY_APP_HOME="FIXME"
MY_APP_JAR="FIXME"

java -jar $MY_APP_HOME/$MY_APP_JAR &
echo $! > $MY_APP_HOME/my-app.pid
exit 0
