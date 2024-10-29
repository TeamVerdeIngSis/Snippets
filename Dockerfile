# Dockerfile for Kotlin Spring Boot Project using Multi-Stage Build
FROM gradle:7.6-jdk21 AS builder
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Second stage: Create a lightweight image for running the application
FROM eclipse-temurin:21-jdk-slim
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
