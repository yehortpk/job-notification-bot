FROM maven:3.9.6-eclipse-temurin-21-alpine as builder

WORKDIR /opt/app
COPY subscriber-bot/pom.xml /opt/app/subscriber-bot/pom.xml
COPY pom.xml /opt/app/pom.xml

COPY subscriber-bot/src /opt/app/subscriber-bot/src

COPY parser/pom.xml /opt/app/parser/pom.xml
COPY router/pom.xml /opt/app/router/pom.xml

RUN --mount=type=cache,target=/root/.m2 mvn clean install -pl subscriber-bot -DskipTests

FROM eclipse-temurin:21.0.2_13-jre as runner

WORKDIR /app
ENV PATH="/usr/local/bin:${PATH}"

COPY --from=builder /opt/app/subscriber-bot/target/subscriber-bot-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5007", "/app/subscriber-bot-0.0.1-SNAPSHOT.jar"]