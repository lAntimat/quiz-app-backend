package com.quiz.repository.models

import kotlinx.serialization.Serializable

@Serializable
data class QuizGenerationParams(
    val theme: String,
    val questionCount: Int,
    val variants: Int,
    val level: Int,
    val comments: String
)