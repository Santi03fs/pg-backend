# 1. Usar Maven y Java 21 para compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .

# Limitar memoria de Maven y evitar saturar logs con descargas
ENV MAVEN_OPTS="-Xmx512m -XX:+UseSerialGC"
RUN mvn clean package -DskipTests --no-transfer-progress

# 2. Usar Java 21 ligero para ejecutarlo
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Limitar memoria de ejecución para Render Free Tier (512MB max)
ENTRYPOINT ["java", "-Xmx350m", "-Xss512k", "-jar", "app.jar"]