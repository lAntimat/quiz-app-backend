package com.quiz.database

import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val id: Long,
    val quizId: Long,
    val title: String,
    val variants: List<String>,
    val answerKey: Int,
    val imageUrl: String? = null,
    val hint: String,
    val orderPosition: Int
)