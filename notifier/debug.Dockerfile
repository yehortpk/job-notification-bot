FROM gradle:8.6.0-jdk21 as notifier
WORKDIR /app

# Copy the Gradle build files (e.g., build.gradle and settings.gradle)
# COPY build.gradle settings.gradle /app/
# COPY src /app/src

# Install wget to download ChromeDriver
RUN apt-get update && apt-get install -y wget gnupg unzip

# Download and install ChromeDriver
RUN wget https://chromedriver.storage.googleapis.com/109.0.5414.25/chromedriver_linux64.zip \
    && unzip chromedriver_linux64.zip -d /usr/local/bin \
    && rm chromedriver_linux64.zip

RUN wget --no-verbose -O /tmp/chrome.deb https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_109.0.5414.119-1_amd64.deb \
      && apt install -y /tmp/chrome.deb \
      && rm /tmp/chrome.deb

ENV PATH="/usr/local/bin:${PATH}"
ENV SPRING_PROFILES_ACTIVE=debug

# Build and run the application using Gradle
CMD ["gradle", "run"]