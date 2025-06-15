# Шаг 1: Сборка JAR
FROM gradle:7.4-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Шаг 2: Запуск
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]