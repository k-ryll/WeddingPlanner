# Stage 1: Build the application JAR
FROM maven:3.8-openjdk-17-slim AS build
WORKDIR /app
# Copy only necessary files for build
COPY pom.xml ./
# COPY .mvn/ .mvn
# COPY mvnw pom.xml ./
# Ensure mvnw is executable
# RUN chmod +x mvnw
# RUN ./mvnw dependency:go-offline -B
COPY src ./src
# Use system mvn command provided by the base image
RUN mvn package -DskipTests -B

# Stage 2: Create the final lightweight image
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar application.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "application.jar"] 