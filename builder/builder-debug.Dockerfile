FROM maven:3.9.6-eclipse-temurin-21-alpine as builder

WORKDIR /opt/app
COPY ../notifier/pom.xml /opt/app/notifier/pom.xml
COPY ../router/pom.xml /opt/app/router/pom.xml
COPY ../subscriber-bot/pom.xml /opt/app/subscriber-bot/pom.xml
COPY ../pom.xml /opt/app/pom.xml

COPY ../notifier/src /opt/app/notifier/src
COPY ../router/src /opt/app/router/src
COPY ../subscriber-bot/src /opt/app/subscriber-bot/src

RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests

FROM eclipse-temurin:21.0.2_13-jre

WORKDIR /app

COPY --from=builder /opt/app/notifier/target/notifier-0.0.1-SNAPSHOT.jar .
COPY --from=builder /opt/app/router/target/router-0.0.1-SNAPSHOT.jar .
COPY --from=builder /opt/app/subscriber-bot/target/subscriber-bot-0.0.1-SNAPSHOT.jar .