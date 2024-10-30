
FROM --platform=linux/amd64 gradle:8-jdk21 AS build

COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon


# Second stage: Create a lightweight image for running the application

FROM --platform=linux/amd64 openjdk:21-slim
EXPOSE 8082
COPY --from=builder /home/gradle/src/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]