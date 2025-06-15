# Этап сборки (Build)
FROM gradle:8.4-jdk17 AS build
WORKDIR /app

# Копируем исходный код
COPY build.gradle.kts .
COPY settings.gradle.kts .
COPY src ./src

# Собираем JAR (включая зависимости)
RUN gradle build --no-daemon

# Этап запуска (Run)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Копируем собранный JAR из этапа сборки
COPY --from=build /app/build/libs/*.jar ./ktor-app.jar

# Открываем порт (по умолчанию Ktor использует 8080)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "ktor-app.jar"]