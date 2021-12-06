FROM maven AS build
COPY . /usr/src/myapp
WORKDIR /usr/src/myapp
RUN mvn -P local clean package
FROM tomcat:8.5.72 AS final
COPY --from=build /usr/src/myapp/target/grimoire.war /usr/local/tomcat/webapps/
RUN wget https://jdbc.postgresql.org/download/postgresql-42.3.1.jar -P lib
# CMD /usr/local/tomcat/bin/startup.sh