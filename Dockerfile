# First stage: Build the application
FROM gradle:8-jdk21 AS build

COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Second stage: Create a lightweight image for running the application
FROM openjdk:21-slim
EXPOSE 8082
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

COPY ./newRelicAgent/newrelic.jar /app/newrelic.jar
COPY ./newRelicAgent/newrelic.yml /app/newrelic.yml
ENTRYPOINT ["java", "-jar", "/app.jar", "-javaagent:/app/newrelic.jar"]
