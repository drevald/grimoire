# Build stage
FROM gradle:8.11.1-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootWar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/grimoire.war /app/grimoire.war
RUN apt-get update && \
    apt-get -y install gettext dos2unix nano && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Copy and prepare startup script
COPY startup.sh ./startup.sh
RUN dos2unix -o ./startup.sh 2>/dev/null || true && \
    chmod u+x ./startup.sh

EXPOSE 8080

CMD [ "/app/startup.sh" ]