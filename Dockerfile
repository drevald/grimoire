FROM maven AS build
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN mvn -P local clean package
FROM tomcat:8.5.72 AS final
COPY --from=build /usr/src/myapp/target/grimoire.war /usr/local/tomcat/webapps/
COPY --from=build /usr/src/myapp/target/grimoire/WEB-INF/lib/postgresql-42.3.0.jar /usr/local/tomcat/lib/
RUN sed -i -e 's/8080/${http.port}/' /usr/local/tomcat/conf/server.xml
CMD /usr/local/openjdk-11/bin/java -Dhttp.port=$PORT -Djava.util.logging.config.file=/usr/local/tomcat/conf/logging.properties -Xms256m -Xmx256m -Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager -Djdk.tls.ephemeralDHKeySize=2048 -Djava.protocol.handler.pkgs=org.apache.catalina.webresources -Dorg.apache.catalina.security.SecurityListener.UMASK=0027 -Dignore.endorsed.dirs= -classpath /usr/local/tomcat/bin/bootstrap.jar:/usr/local/tomcat/bin/tomcat-juli.jar -Dcatalina.base=/usr/local/tomcat -Dcatalina.home=/usr/local/tomcat -Djava.io.tmpdir=/usr/local/tomcat/temp org.apache.catalina.startup.Bootstrap start
