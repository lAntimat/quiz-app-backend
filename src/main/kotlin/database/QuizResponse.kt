package com.quiz.database

import QuestionEntity
import kotlinx.serialization.Serializable

@Serializable
data class QuizResponse(
    val quizId: Long = 0,
    val title: String,
    val createdAt: Long,
    val difficulty: Int,
    val questionCount: Long = 0,
    val questions: List<QuestionResponse>? = listOf()
)