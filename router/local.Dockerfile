FROM maven:3.9.6-eclipse-temurin-21-alpine as builder

WORKDIR /opt/app
COPY router/pom.xml /opt/app/router/pom.xml
COPY pom.xml /opt/app/pom.xml

COPY router/src /opt/app/router/src

COPY notifier/pom.xml /opt/app/notifier/pom.xml
COPY subscriber-bot/pom.xml /opt/app/subscriber-bot/pom.xml

RUN --mount=type=cache,target=/root/.m2 mvn clean install -pl router -DskipTests

FROM eclipse-temurin:21.0.2_13-jre as runner

WORKDIR /app
ENV PATH="/usr/local/bin:${PATH}"

COPY --from=builder /opt/app/router/target/router-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "/app/notifier-0.0.1-SNAPSHOT.jar"]