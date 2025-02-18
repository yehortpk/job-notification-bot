FROM maven:3.9.6-eclipse-temurin-21-alpine as builder

WORKDIR /opt/app

RUN apk add --no-cache git

RUN git clone https://github.com/yehortpk/job-notification-bot.git
RUN mv job-notification-bot/* .

RUN --mount=type=cache,target=/root/.m2 mvn clean install -pl router -DskipTests

FROM eclipse-temurin:21.0.2_13-jre as runner

WORKDIR /app
ENV PATH="/usr/local/bin:${PATH}"

ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=builder /opt/app/router/target/router-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "/app/router-0.0.1-SNAPSHOT.jar"]