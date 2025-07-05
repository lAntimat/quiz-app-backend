package com.quiz.usecases

import com.quiz.database.QuestionResponse
import com.quiz.database.QuizResponse
import com.quiz.database.database.Quiz
import com.quiz.json
import com.quiz.repository.QuizRepository
import com.quiz.repository.models.OpenApiResponse
import com.quiz.repository.models.QuizGenerationParams
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateQuizUseCase {

    private val quizRepository = QuizRepository()

    suspend fun invoke(
        oldQuizId: Long?,
        result: OpenApiResponse,
        params: QuizGenerationParams
    ): QuizResponse? {

        return handleSuccess(oldQuizId, result, params)

    }

    @OptIn(ExperimentalTime::class)
    private suspend fun handleSuccess(
        oldQuizId: Long?,
        result: OpenApiResponse,
        params: QuizGenerationParams,
    ): QuizResponse? {
        val contentJson = json.decodeFromString<List<QuestionResponse>>(
            result.choices.first().message.content
                .replace("```json", "")  // Удаляем маркеры кода
                .replace("```", "")
                .trim()
        )

        println(contentJson)

        val quizId = quizRepository.insertQuiz(
            Quiz(
                title = params.theme,
                createdAt = Clock.System.now().toEpochMilliseconds(),
                difficulty = params.level,
            )
        )
        contentJson.forEachIndexed { index, question ->
            quizRepository.insertQuestion(question, quizId)
        }

        return quizRepository.getQuizById(quizId)
    }

}