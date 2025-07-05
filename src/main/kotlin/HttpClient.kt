package com.quiz

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import kotlinx.serialization.json.Json

val httpClient = HttpClient() {

    install(ContentNegotiation) {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL // или LogLevel.ALL
    }

    // Таймаут на выполнение запроса (socket timeout)
    install(HttpTimeout) {
        requestTimeoutMillis = 300_000L // 5 минут
        connectTimeoutMillis = 300_000L
        socketTimeoutMillis = 300_000L
    }
}