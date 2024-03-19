FROM ubuntu:latest as git
WORKDIR /app

ARG PROJECT_URL
ARG PROJECT_PATH

RUN  apt-get -yq update && \
     apt-get -yqq install ssh git
# Copy the SSH key to the container
RUN mkdir -p /root/.ssh && \
    chmod 700 /root/.ssh && \
    ssh-keyscan github.com > /root/.ssh/known_hosts
COPY ./id_rsa /root/.ssh/id_rsa
RUN chmod 600 /root/.ssh/id_rsa

RUN echo "StrictHostKeyChecking no" >> /etc/ssh/ssh_config

RUN git clone -b change-to-maven-modules $PROJECT_URL
RUN mv $PROJECT_PATH/* .
RUN rm -rf $PROJECT_PATH

FROM maven:3.9.6-eclipse-temurin-21-alpine as builder

WORKDIR /opt/app
COPY --from=git /app/notifier/pom.xml /opt/app/notifier/pom.xml
COPY --from=git /app/router/pom.xml /opt/app/router/pom.xml
COPY --from=git /app/subscriber-bot/pom.xml /opt/app/subscriber-bot/pom.xml
COPY --from=git /app/pom.xml /opt/app/pom.xml

COPY --from=git /app/notifier/src /opt/app/notifier/src
COPY --from=git /app/router/src /opt/app/router/src
COPY --from=git /app/subscriber-bot/src /opt/app/subscriber-bot/src

RUN --mount=type=cache,target=/root/.m2 mvn clean install -DskipTests

FROM eclipse-temurin:21.0.2_13-jre

WORKDIR /app

COPY --from=builder /opt/app/notifier/target/notifier-0.0.1-SNAPSHOT.jar .
COPY --from=builder /opt/app/router/target/router-0.0.1-SNAPSHOT.jar .
COPY --from=builder /opt/app/subscriber-bot/target/subscriber-bot-0.0.1-SNAPSHOT.jar .