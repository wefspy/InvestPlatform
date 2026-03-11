# --- Stage 1: Build ---
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY mvnw pom.xml ./
COPY .mvn .mvn

# Download dependencies (cached layer)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src src

RUN ./mvnw clean package -DskipTests -B

# --- Stage 2: Run ---
FROM eclipse-temurin:21-jre

RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
