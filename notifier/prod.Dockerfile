FROM ubuntu:latest as notifier-prod-build
WORKDIR /app

# Copy the Gradle build files (e.g., build.gradle and settings.gradle)
# COPY build.gradle settings.gradle /app/
# COPY src /app/src

# Install wget to download ChromeDriver
RUN apt-get update && apt-get install -y wget gnupg unzip git ssh

# Download and install ChromeDriver
RUN wget https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip \
    && unzip chromedriver_linux64.zip -d /usr/local/bin \
    && rm chromedriver_linux64.zip

RUN wget --no-verbose -O /tmp/chrome.deb https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_114.0.5735.90-1_amd64.deb \
      && apt install -y /tmp/chrome.deb \
      && rm /tmp/chrome.deb

ARG PROJECT_URL

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

COPY --from=notifier-prod-build /app/$PROJECT_PATH/notifier/src /app/src
# Copy the Gradle build files (e.g., build.gradle and settings.gradle)
COPY --from=notifier-prod-build /app/$PROJECT_PATH/notifier/build.gradle /app/build.gradle

ENV PATH="/usr/local/bin:${PATH}"
ENV SPRING_PROFILES_ACTIVE=prod

# Build and run the application using Gradle
CMD ["gradle", "run"]