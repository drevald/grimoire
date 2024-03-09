#!/bin/bash
export JAVA_OPTS="$JAVA_OPTS -Xms256m -Xmx256m"
sed -i "s/port=\"8080\"/port=\"$PORT\"/" /usr/local/tomcat/conf/server.xml
. /usr/local/tomcat/bin/catalina.sh run