# First stage: Build the application
FROM gradle:8-jdk21 AS build

COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Second stage: Create a lightweight image for running the application
FROM openjdk:21-slim
EXPOSE 8082
RUN mkdir /app
COPY --from=build /home/gradle/src/build/libs/*.jar  /app/app.jar

COPY ./newrelic/newrelic.jar /app/newrelic.jar
COPY ./newrelic/newrelic.yml /app/newrelic.yml

ENTRYPOINT ["java","-javaagent:/app/newrelic.jar","-jar","/app/app.jar"]
