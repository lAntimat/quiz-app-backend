package com.quiz.repository.models

data class QuizGenerationParams(
    val theme: String,
    val questionCount: Int,
    val variants: Int,
    val level: Int,
    val comments: String
)