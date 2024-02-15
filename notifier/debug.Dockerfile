FROM gradle:8.6.0-jdk21 as notifier
WORKDIR /app

# Copy the Gradle build files (e.g., build.gradle and settings.gradle)
COPY build.gradle settings.gradle /app/
COPY src /app/src

ENV SPRING_PROFILES_ACTIVE=debug

# Build and run the application using Gradle
CMD ["gradle", "run"]