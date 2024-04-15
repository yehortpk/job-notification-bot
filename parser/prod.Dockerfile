FROM eclipse-temurin:21.0.2_13-jre as runner

WORKDIR /app
ENV PATH="/usr/local/bin:${PATH}"

RUN \
if [ ! -f /usr/local/bin/chromedriver ];  then \
    apt-get update && apt-get install -y wget gnupg unzip && \
    # Download and install ChromeDriver
    wget https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip  &&\
    unzip chromedriver_linux64.zip -d /usr/local/bin &&\
    rm chromedriver_linux64.zip; \
fi

RUN \
if [ ! -f /usr/bin/google-chrome ];  then \
    apt-get update && apt-get install -y wget gnupg &&\
    # Download and install Chrome
    wget -O /tmp/chrome.deb https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_114.0.5735.90-1_amd64.deb &&\
    apt install -y /tmp/chrome.deb && rm /tmp/chrome.deb; \
fi

COPY target/parser-0.0.1-SNAPSHOT.jar .

ENV SPRING_PROFILES_ACTIVE=prod

CMD ["java", "-jar", "/app/parser-0.0.1-SNAPSHOT.jar"]