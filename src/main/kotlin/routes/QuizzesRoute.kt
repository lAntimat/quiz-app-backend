package com.quiz.routes

import com.quiz.database.QuestionResponse
import com.quiz.database.QuizResponse
import com.quiz.httpClient
import com.quiz.json
import com.quiz.repository.QuizRepository
import com.quiz.repository.models.OpenApiMessage
import com.quiz.repository.models.OpenApiRequest
import com.quiz.repository.models.OpenApiResponse
import com.quiz.repository.models.QuizGenerationParams
import com.quiz.usecases.CreateQuizUseCase
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.plugins.NotFoundException
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import kotlinx.serialization.json.Json
import kotlin.text.toLongOrNull

fun Routing.configureQuizzesRoute(
    token: String,
    quizRepository: QuizRepository
) {
    post("/api/1/generateQuiz") {
        try {
            // Вариант 1 (рекомендуемый, когда настроен ContentNegotiation)
            val params = call.receive<QuizGenerationParams>()

            // Вариант 2 (ручная десериализация)
//                val rawJson = call.receiveText()
//                val params = Json.decodeFromString<QuizGenerationParams>(rawJson)

            val requestText = """
                Придумай квиз на тему ${params.theme} с количеством вопросом: ${params.questionCount} и вариантами ответа: ${params.variants} штуки.
                Постарайся придумать разноообразные вопросы. Сложность квиза должна быть на ${params.level} из 3.
                Ответ надо вернуть в виде списка с такой структурой json:
                {
                  "title": "Пример вопроса",
                  "variants": [
                    "Ответ 1",
                    "Ответ 2",
                    "Ответ 3"
                  ],
                  "answerKey": 0,
                  "hint": "Подсказка",
                }
                Пояснение полей:
                title — строка с текстом вопроса.
                variants — массив строк с вариантами ответов.
                answerKey — индекс правильного ответа в массиве variants (начинается с 0).
                hint - "Небольшая подсказка".
                
                Ответы на вопросы в массиве variants должны быть перемешаны.
                
                И дополнительные пожелания для создания вопросов ${params.comments}
            """.trimIndent()

            val request = OpenApiRequest(
                model = "deepseek/deepseek-r1:free",
                messages = listOf(
                    OpenApiMessage(
                        role = "user",
                        content = requestText
                    )
                )
            )

            // 2. Делаем запрос к внешнему API
            val openApiRequestText = Json.encodeToString(request)
            val response: HttpResponse = httpClient.post("https://openrouter.ai/api/v1/chat/completions") {
                setBody(openApiRequestText)
                contentType(ContentType.Application.Json)

                header("Authorization", "Bearer $token")
                header("User-Agent", "MyApp/1.0")
            }

            val responseText = response.bodyAsText()
            val openApiJson = json.decodeFromString<OpenApiResponse>(responseText)
            val quizResponse = CreateQuizUseCase().invoke(
                oldQuizId = null,
                result = openApiJson,
                params = params
            )

            // 3. Возвращаем ответ от внешнего API
            call.respondText(
                text = json.encodeToString(quizResponse),
                status = response.status,
                contentType = response.contentType()
            )

        } catch (e: Exception) {
            call.respondText(
                "Error: ${e.message}",
                status = HttpStatusCode.InternalServerError,
            )
        }
    }

    route("/quizzes") {
        // Создать новую викторину
        post {
//                val quiz = call.receive<QuizResponse>()
//                val id = quizRepository.insertQuiz(quiz)
//                call.respond(HttpStatusCode.Created, mapOf("id" to id))
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

                quiz.questions?.forEach {
                    quizRepository.updateQuestion(it, quiz.quizId)
                }
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
                    val id = quizRepository.insertQuestion(question, quizId)
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
                        quizRepository.updateQuestion(question, question.quizId ?: 0L)
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