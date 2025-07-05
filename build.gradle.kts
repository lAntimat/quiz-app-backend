plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    kotlin("plugin.serialization") version "1.9.0"
}

group = "com.quiz"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

ktor {
    docker {
        jreVersion.set(JavaVersion.VERSION_17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)

    // Основные серверные зависимости
    implementation("io.ktor:ktor-server-core:2.3.3")
    implementation("io.ktor:ktor-server-netty:2.3.3") // Или другой engine (jetty, cio и т.д.)

    // Сериализация
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.3")

    // Логирование
    implementation("io.ktor:ktor-server-call-logging:2.3.3")
    implementation("ch.qos.logback:logback-classic:1.4.7") // Реализация для логов

    // Дополнительные полезные плагины
    implementation("io.ktor:ktor-server-status-pages:2.3.3") // Обработка ошибок
    implementation("io.ktor:ktor-server-default-headers:2.3.3") // Стандартные заголовки

    implementation("io.ktor:ktor-client-core:3.2.0")
    implementation("io.ktor:ktor-client-cio:3.2.0")
    implementation("io.ktor:ktor-client-logging:3.2.0")
    implementation("io.ktor:ktor-client-content-negotiation:3.2.0")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.0")

    implementation("io.ktor:ktor-server-cors:3.2.0")

    // Exposed
    implementation("org.jetbrains.exposed:exposed-core:0.41.1")
    implementation("org.jetbrains.exposed:exposed-dao:0.41.1")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.41.1")

    // PostgreSQL драйвер
    implementation("org.postgresql:postgresql:42.5.4")

    // Kotlinx Serialization для JSON
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")

    // Для работы с датами (если нужно)
    implementation("org.jetbrains.exposed:exposed-java-time:0.41.1")

    // Пароль хеширование
    implementation("at.favre.lib:bcrypt:0.10.2")

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
