FROM eclipse-temurin:21.0.2_13-jre as runner

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod
COPY target/subscriber-bot-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "/app/subscriber-bot-0.0.1-SNAPSHOT.jar"]