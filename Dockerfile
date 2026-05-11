# 1. Usar Maven y Java 17 (Eclipse Temurin) para compilar el proyecto
FROM maven:3.8.5-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 2. Usar Java 17 (Eclipse Temurin) ligero para ejecutarlo
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]