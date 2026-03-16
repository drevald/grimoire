#!/bin/bash
mkdir -p $LOCAL_STORAGE

# Set Java options for memory and debugging
export JAVA_OPTS="-Xms256m -Xmx256m"
export DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,address=*:8000,server=y,suspend=n"

# Run Spring Boot application with debug enabled
exec java $JAVA_OPTS $DEBUG_OPTS -jar /app/grimoire.war