FROM node:23 as playwright
WORKDIR /usr/app

RUN mkdir -p /ms-playwright
RUN npm install playwright@1.49.0
RUN PLAYWRIGHT_BROWSERS_PATH=/ms-playwright npx playwright install

FROM maven:3.9.6-eclipse-temurin-21-alpine as builder


WORKDIR /opt/app

RUN apk add --no-cache git

RUN git clone https://github.com/yehortpk/job-notification-bot.git
RUN mv job-notification-bot/* .

RUN --mount=type=cache,target=/root/.m2 mvn clean install -pl parser -DskipTests


FROM eclipse-temurin:21.0.2_13-jre as runner

WORKDIR /app
ENV PATH="/usr/local/bin:${PATH}"

RUN apt-get update && apt-get install -y \
    libatk1.0-0 \
    libatk-bridge2.0-0 \
    libcups2 \
    libdrm2 \
    libdbus-1-3 \
    libxkbcommon0 \
    libxcomposite1 \
    libxdamage1 \
    libxfixes3 \
    libxrandr2 \
    libgbm1 \
    libpango-1.0-0 \
    libcairo2 \
    libasound2 \
    libnspr4 \
    libnss3 \
    libx11-xcb1 \
    libxcursor1 \
    libgtk-3-0 \
    libpangocairo-1.0-0 \
    libcairo-gobject2 \
    libgdk-pixbuf-2.0-0 \
    libatomic1 \
    libxslt1.1 \
    libwoff1 \
    libvpx7 \
    libevent-2.1-7 \
    libopus0 \
    libflite1 \
    libwebp7 \
    libwebpdemux2 \
    libwebpmux3 \
    libavif13 \
    libharfbuzz-icu0 \
    libenchant-2-2 \
    libsecret-1-0 \
    libhyphen0 \
    libmanette-0.2-0 \
    libgles2 \
    libx264-163 \
    gstreamer1.0-plugins-base \
    gstreamer1.0-plugins-good \
    gstreamer1.0-tools \
    gstreamer1.0-libav \
    gstreamer1.0-alsa \
    gstreamer1.0-plugins-ugly \
    gstreamer1.0-plugins-bad \
    libgstreamer1.0-0 \
    && rm -rf /var/lib/apt/lists/*


ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright
ENV PLAYWRIGHT_SKIP_BROWSER_GC=1
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

ENV SPRING_PROFILES_ACTIVE=prod

COPY --from=playwright /ms-playwright /ms-playwright
COPY --from=builder /opt/app/parser/target/parser-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "/app/parser-0.0.1-SNAPSHOT.jar"]