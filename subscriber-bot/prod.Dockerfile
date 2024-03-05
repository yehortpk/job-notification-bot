FROM ubuntu:latest as subscriber-bot-prod-git
WORKDIR /app

ARG PROJECT_URL

RUN  apt-get -yq update && \
     apt-get -yqq install ssh git
# Copy the SSH key to the container
RUN mkdir -p /root/.ssh && \
    chmod 700 /root/.ssh && \
    ssh-keyscan github.com > /root/.ssh/known_hosts
COPY ./id_rsa /root/.ssh/id_rsa
RUN chmod 600 /root/.ssh/id_rsa

RUN echo "StrictHostKeyChecking no" >> /etc/ssh/ssh_config

RUN git clone $PROJECT_URL

FROM gradle:8.6.0-jdk21 as notifier-prod

ARG PROJECT_PATH

COPY --from=subscriber-bot-prod-git /app/$PROJECT_PATH/subscriber-bot/src /app/src
# Copy the Gradle build files (e.g., build.gradle and settings.gradle)
COPY --from=subscriber-bot-prod-git /app/$PROJECT_PATH/subscriber-bot/build.gradle /app/build.gradle

ENV SPRING_PROFILES_ACTIVE=prod

# Build and run the application using Gradle
CMD ["gradle", "run"]