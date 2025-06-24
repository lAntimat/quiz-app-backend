package com.quiz.usecases

import com.quiz.database.QuizResponse
import com.quiz.repository.QuizRepository
import com.quiz.repository.models.GeneratedQuestionResponse
import com.quiz.repository.models.OpenApiResponse
import com.quiz.repository.models.QuizGenerationParams
import kotlinx.serialization.json.Json
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateQuizUseCase {

    private val quizRepository = QuizRepository()

    suspend fun invoke(
        oldQuizId: Long?,
        result: OpenApiResponse,
        params: QuizGenerationParams
    ) {

        handleSuccess(oldQuizId, result, params)

    }

    @OptIn(ExperimentalTime::class)
    private suspend fun handleSuccess(
        oldQuizId: Long?,
        result: OpenApiResponse,
        params: QuizGenerationParams,
    ): List<GeneratedQuestionResponse> {
        val contentJson = Json.decodeFromString<List<GeneratedQuestionResponse>>(
            result.choices.first().message.content
                .replace("```json", "")  // Удаляем маркеры кода
                .replace("```", "")
                .trim()
        )

        println(contentJson)

        val quizId = oldQuizId ?: Random.nextLong()

        quizRepository.insertQuiz(
            QuizResponse(
                quizId = quizId,
                title = params.theme,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                difficulty = params.level
            )
        )
        contentJson.forEachIndexed { index, question ->
            //dataSourceManager.insertQuestions(question.toEntity(quizId, index))
        }

        return contentJson
    }

}