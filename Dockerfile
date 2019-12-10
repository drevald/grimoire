FROM tomcat
ADD target/grimoire.war /webapps/
EXPOSE 8080
CMD ["catalina.sh", "run"]

