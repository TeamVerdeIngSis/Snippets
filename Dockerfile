# First stage: Build the application
FROM gradle:8-jdk21 AS build

COPY . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

# Second stage: Create a lightweight image for running the application
FROM openjdk:21-slim
EXPOSE 8082
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

RUN mkdir -p /usr/local/newrelic
ADD ./newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD ./newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml
ENTRYPOINT ["java","-javaagent:/usr/local/newrelic/newrelic.jar","-jar","/app.jar"]
