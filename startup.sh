#!/bin/bash
mkdir $LOCAL_STORAGE
sed -i "s/port=\"8080\"/port=\"$PORT\"/" /usr/local/tomcat/conf/server.xml
export JAVA_OPTS="$JAVA_OPTS -Xms256m -Xmx256m"
export JPDA_OPTS="-agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n"
. /usr/local/tomcat/bin/catalina.sh jpda run