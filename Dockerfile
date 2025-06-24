# Building from a base docker image that contains jdk 17
FROM openjdk:17-slim

# Maintainter
LABEL authors="Ikenumah (enumahinm@gmail.com)"

# Set the working directory
WORKDIR /app

# Add the application jar file to the image
COPY target/patient-0.0.1-SNAPSHOT.jar patient-microservice.jar

# When a container starts from this image, run the jar
ENTRYPOINT ["java", "-jar", "patient-microservice.jar"]