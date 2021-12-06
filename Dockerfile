FROM maven AS build
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN mvn -P local clean package
FROM tomcat:8.5.72 AS final
COPY --from=build /usr/src/myapp/target/grimoire.war /usr/local/tomcat/webapps/
COPY --from=build /usr/src/myapp/target/grimoire/WEB-INF/lib/postgresql-42.3.0.jar /usr/local/tomcat/lib/