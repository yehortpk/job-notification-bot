FROM maven:3.9.6-eclipse-temurin-21-alpine as builder

WORKDIR /opt/app
COPY parser/pom.xml /opt/app/parser/pom.xml
COPY pom.xml /opt/app/pom.xml

COPY parser/src /opt/app/parser/src
COPY router/pom.xml /opt/app/router/pom.xml
COPY subscriber-bot/pom.xml /opt/app/subscriber-bot/pom.xml

RUN --mount=type=cache,target=/root/.m2 mvn clean install -pl parser -DskipTests

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
    wget -O /tmp/chrome.deb https://mirror.cs.uchicago.edu/google-chrome/pool/main/g/google-chrome-stable/google-chrome-stable_114.0.5735.90-1_amd64.deb &&\
    apt install -y /tmp/chrome.deb && rm /tmp/chrome.deb; \
fi

COPY --from=builder /opt/app/parser/target/parser-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "/app/parser-0.0.1-SNAPSHOT.jar"]