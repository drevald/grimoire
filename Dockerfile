# Build stage
FROM gradle:8.11.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootWar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/grimoire.war /app/grimoire.war
# Copy and prepare startup script
COPY startup.sh ./startup.sh
RUN sed -i 's/\r$//' ./startup.sh && chmod u+x ./startup.sh

EXPOSE 8080

CMD [ "/app/startup.sh" ]