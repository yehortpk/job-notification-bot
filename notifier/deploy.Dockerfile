FROM project-builder:latest as builder

WORKDIR /app
ENV PATH="/usr/local/bin:${PATH}"

RUN apt-get update && apt-get install -y wget gnupg unzip

# Download and install ChromeDriver
RUN wget https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip \
    && unzip chromedriver_linux64.zip -d /usr/local/bin \
    && rm chromedriver_linux64.zip

RUN wget --no-verbose -O /tmp/chrome.deb https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_114.0.5735.90-1_amd64.deb \
      && apt install -y /tmp/chrome.deb \
      && rm /tmp/chrome.deb


ENV SPRING_PROFILES_ACTIVE=deploy

CMD ["java", "-jar", "/app/notifier-0.0.1-SNAPSHOT.jar"]