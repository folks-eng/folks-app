#!/bin/sh

export DEBUG_OPTS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=9791,server=y,suspend=n"
#export JAVA_OPTS="-Dlog4j2.configurationFile=log4j2-cb.xml -Dorg.slf4j.simpleLogger.defaultLogLevel=off"

java $DEBUG_OPTS $JAVA_OPTS -Dhttp.port=8443 -Dhttp.ssl=true -jar target/folks-app-0.0.1-SNAPSHOT.jar
