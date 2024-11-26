# First stage: Build the application
FROM gradle:8-jdk21 AS build

# Agregar argumentos para autenticación
ARG USERNAME
ARG PAT_TOKEN

# Configurar las credenciales para repositorios privados
ENV GITHUB_ACTOR=$USERNAME
ENV GITHUB_TOKEN=$PAT_TOKEN

# Copiar el código fuente
COPY . /home/gradle/src
WORKDIR /home/gradle/src

# Construir el proyecto
RUN gradle build --no-daemon

# Second stage: Create a lightweight image for running the application
FROM openjdk:21-slim
EXPOSE 8082
RUN mkdir /app

# Copiar el archivo JAR generado en la etapa anterior
COPY --from=build /home/gradle/src/build/libs/*.jar /app/app.jar

# Copiar los archivos de configuración de New Relic
COPY ./newrelic/newrelic.jar /app/newrelic.jar
COPY ./newrelic/newrelic.yml /app/newrelic.yml

# Configurar el comando de entrada
ENTRYPOINT ["java", "-javaagent:/app/newrelic.jar", "-jar", "/app/app.jar"]
