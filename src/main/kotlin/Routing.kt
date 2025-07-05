package com.quiz

import com.quiz.repository.QuizRepository
import com.quiz.routes.authController
import com.quiz.routes.configureQuizzesRoute
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import userRepository

val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
    isLenient = true
    explicitNulls = false
}

fun Application.configureRouting() {
    val token = System.getenv("DEEPSEEK_TOKEN")

    val logger = LoggerFactory.getLogger("RequestLogger")

    val quizRepository = QuizRepository()

    routing {
        get("/") {
            call.respondText("Hello World!")
        }
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

        configureQuizzesRoute(token, quizRepository)

        authController(userRepository)
    }
}
