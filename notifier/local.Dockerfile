FROM maven:3.9.6-eclipse-temurin-21-alpine as builder

WORKDIR /opt/app
COPY notifier/pom.xml /opt/app/notifier/pom.xml
COPY pom.xml /opt/app/pom.xml

COPY notifier/src /opt/app/notifier/src
COPY router/pom.xml /opt/app/router/pom.xml
COPY subscriber-bot/pom.xml /opt/app/subscriber-bot/pom.xml

RUN --mount=type=cache,target=/root/.m2 mvn clean install -pl notifier -DskipTests

FROM eclipse-temurin:21.0.2_13-jre as runner

WORKDIR /app
ENV PATH="/usr/local/bin:${PATH}"

RUN \
if [ -x "$(command -v /usr/local/bin/chromedriver)" ];  then \
    apt-get update && apt-get install -y wget gnupg unzip \
    # Download and install ChromeDriver
    wget https://chromedriver.storage.googleapis.com/114.0.5735.90/chromedriver_linux64.zip \
    # Download Chrome deb (cached layer)
    wget --no-verbose -O /tmp/chrome.deb https://dl.google.com/linux/chrome/deb/pool/main/g/google-chrome-stable/google-chrome-stable_114.0.5735.90-1_amd64.deb \
    unzip chromedriver_linux64.zip -d /usr/local/bin \
    rm chromedriver_linux64.zip; \
fi

RUN \
if [ -x "$(command -v /usr/bin/google-chrome)" ];  then \
    apt update \
    # Install Chrome using cached deb (if available)
    apt install -y /tmp/chrome.deb && rm /tmp/chrome.deb; \
fi

COPY --from=builder /opt/app/notifier/target/notifier-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "/app/notifier-0.0.1-SNAPSHOT.jar"]