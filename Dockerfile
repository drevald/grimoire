FROM maven AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests
FROM tomcat:9-jre11 AS final
WORKDIR /app
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps/
RUN apt-get update
RUN apt-get -y install gettext dos2unix nano
COPY startup.sh .
RUN dos2unix -o ./startup.sh
RUN chmod u+x ./startup.sh
COPY --from=build /app/target/ROOT/WEB-INF/lib/postgresql-42.7.2.jar /usr/local/tomcat/lib/
CMD [ "/app/startup.sh" ]