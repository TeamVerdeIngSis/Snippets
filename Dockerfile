# Use an official Gradle image with JDK 17 for building the application
FROM gradle:8.10-jdk21 AS builder
COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Second stage: Create a lightweight image for running the application
FROM openjdk:21-jdk-slim
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
