FROM maven AS build
WORKDIR /app
COPY . .
RUN mvn -P local clean package -DskipTests
#FROM tomcat:8.5-jre11-slim AS final
FROM tomcat:9-jre11 AS final
WORKDIR /app
COPY --from=build /app/target/grimoire.war /usr/local/tomcat/webapps/
RUN apt-get update
RUN apt-get -y install gettext dos2unix
COPY startup.sh .
RUN dos2unix -o ./startup.sh
RUN chmod u+x ./startup.sh
COPY --from=build /app/target/grimoire/WEB-INF/lib/postgresql-42.3.0.jar /usr/local/tomcat/lib/
CMD [ "/app/startup.sh" ]