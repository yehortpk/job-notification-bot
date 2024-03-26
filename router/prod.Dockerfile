FROM eclipse-temurin:21.0.2_13-jre as runner

WORKDIR /app

ENV SPRING_PROFILES_ACTIVE=prod
COPY target/router-0.0.1-SNAPSHOT.jar .

CMD ["java", "-jar", "/app/router-0.0.1-SNAPSHOT.jar"]