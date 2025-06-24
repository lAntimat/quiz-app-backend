package com.quiz.database

import kotlinx.serialization.Serializable

// DTO (Data Transfer Objects)
@Serializable
data class QuizResponse(
    val quizId: Long,
    val title: String,
    val createdAt: Long,
    val difficulty: Int
)