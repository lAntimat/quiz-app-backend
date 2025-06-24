plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
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
    implementation("io.ktor:ktor-client-core:3.2.0")
    implementation("io.ktor:ktor-client-cio:3.2.0")
    implementation("io.ktor:ktor-client-logging:3.2.0")

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



    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
