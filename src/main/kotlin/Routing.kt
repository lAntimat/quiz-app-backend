package com.quiz

import com.quiz.database.QuestionResponse
import com.quiz.database.QuizResponse
import com.quiz.repository.QuizRepository
import com.quiz.repository.models.OpenApiResponse
import com.quiz.repository.models.QuizGenerationParams
import com.quiz.usecases.CreateQuizUseCase
import io.ktor.client.HttpClient
import io.ktor.client.call.body
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
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.httpMethod
import io.ktor.server.request.receive
import io.ktor.server.request.receiveText
import io.ktor.server.request.uri
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.filter
import io.ktor.util.toMap
import org.slf4j.LoggerFactory

fun Application.configureRouting() {

    val token =
        "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"

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

        post("/api/1/generateQuiz") {
            try {
                // 1. Получаем данные из оригинального запроса
                val requestBody = call.receive<QuizGenerationParams>()
                val headers = call.request.headers

                // Логируем заголовки
                logger.info("Headers: ${call.request.headers.toMap()}")

                // Логируем метод и URL
                logger.info("${call.request.httpMethod.value} ${call.request.uri}")

                // 2. Делаем запрос к внешнему API
                val response: HttpResponse = httpClient.post("https://openrouter.ai/api/v1/chat/completions") {
                    setBody(requestBody)

                    header("Authorization", token)
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

        route("/quizzes") {
            // Создать новую викторину
            post {
                val quiz = call.receive<QuizResponse>()
                val id = quizRepository.insertQuiz(quiz)
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            }

            // Получить все викторины
            get {
                val quizzes = quizRepository.getAllQuizzes()
                call.respond(quizzes)
            }

            // Маршруты для конкретной викторины
            route("/{quizId}") {
                // Получить викторину по ID
                get {
                    val quizId = call.parameters["quizId"]?.toLongOrNull()
                        ?: throw IllegalArgumentException("Invalid quiz ID")

                    val quiz = quizRepository.getQuizById(quizId)
                        ?: throw NotFoundException("Quiz not found")

                    call.respond(quiz)
                }

                // Обновить викторину
                put {
                    val quizId = call.parameters["quizId"]?.toLongOrNull()
                        ?: throw IllegalArgumentException("Invalid quiz ID")

                    val quiz = call.receive<QuizResponse>().copy(quizId = quizId)
                    quizRepository.updateQuiz(quiz)
                    call.respond(HttpStatusCode.OK)
                }

                // Удалить викторину
                delete {
                    val quizId = call.parameters["quizId"]?.toLongOrNull()
                        ?: throw IllegalArgumentException("Invalid quiz ID")

                    quizRepository.deleteQuiz(quizId)
                    call.respond(HttpStatusCode.NoContent)
                }

                // Маршруты для вопросов викторины
                route("/questions") {
                    // Получить все вопросы викторины
                    get {
                        val quizId = call.parameters["quizId"]?.toLongOrNull()
                            ?: throw IllegalArgumentException("Invalid quiz ID")

                        val questions = quizRepository.getQuestionsByQuizId(quizId)
                        call.respond(questions)
                    }

                    // Добавить новый вопрос
                    post {
                        val quizId = call.parameters["quizId"]?.toLongOrNull()
                            ?: throw IllegalArgumentException("Invalid quiz ID")

                        val question = call.receive<QuestionResponse>().copy(quizId = quizId)
                        val id = quizRepository.insertQuestion(question)
                        call.respond(HttpStatusCode.Created, mapOf("id" to id))
                    }

                    // Маршруты для конкретного вопроса
                    route("/{questionId}") {
                        // Получить вопрос по ID
                        get {
                            val questionId = call.parameters["questionId"]?.toLongOrNull()
                                ?: throw IllegalArgumentException("Invalid question ID")

                            val question = quizRepository.getQuestion(questionId)
                                ?: throw NotFoundException("Question not found")

                            call.respond(question)
                        }

                        // Обновить вопрос
                        put {
                            val questionId = call.parameters["questionId"]?.toLongOrNull()
                                ?: throw IllegalArgumentException("Invalid question ID")

                            val question = call.receive<QuestionResponse>().copy(id = questionId)
                            quizRepository.updateQuestion(question)
                            call.respond(HttpStatusCode.OK)
                        }

                        // Удалить вопрос
                        delete {
                            val questionId = call.parameters["questionId"]?.toLongOrNull()
                                ?: throw IllegalArgumentException("Invalid question ID")

                            quizRepository.deleteQuestion(questionId)
                            call.respond(HttpStatusCode.NoContent)
                        }
                    }
                }
            }
        }
    }
}
