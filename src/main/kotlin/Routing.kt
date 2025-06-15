package com.quiz

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.application.*
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receiveText
import io.ktor.server.request.uri
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.filter
import io.ktor.util.toMap
import org.slf4j.LoggerFactory

fun Application.configureRouting() {

    val httpClient = HttpClient() {
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

    val logger = LoggerFactory.getLogger("RequestLogger")

    routing {
        get("/api/v1/chat/completions") {
            call.respondText("Hello World!")
        }

        post("/api/v1/chat/completions") {
            try {
                // 1. Получаем данные из оригинального запроса
                val requestBody = call.receiveText()
                val headers = call.request.headers

                // Логируем заголовки
                logger.info("Headers: ${call.request.headers.toMap()}")

                // Логируем метод и URL
                logger.info("${call.request.httpMethod.value} ${call.request.uri}")

                // 2. Делаем запрос к внешнему API
                val response: HttpResponse = httpClient.post("https://openrouter.ai/api/v1/chat/completions") {
                    setBody(requestBody)
                    headers.filter { name, value -> name == "Authorization" }.forEach { name, values ->
                        values.forEach { value ->
                            header(name, value)
                            logger.info("add header '$name' with value $value to headers")
                        }
                    }

                    header("User-Agent", "MyApp/1.0")

                }

                // 3. Возвращаем ответ от внешнего API
                call.respondText(
                    text = response.bodyAsText(),
                    status = response.status,
                    contentType = response.contentType()
                )

            } catch (e: Exception) {
                call.respondText(
                    "Error: ${e.message}",
                    status = HttpStatusCode.InternalServerError
                )
            }
        }
    }
}
