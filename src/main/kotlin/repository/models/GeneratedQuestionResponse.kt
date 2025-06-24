package com.quiz.repository.models

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class GeneratedQuestionResponse(
    val id: Long = Random.Default.nextLong(),
    val title: String,
    val variants: List<String>,
    val answerKey: Int,
    val imageUrl: String? = null,
    val hint: String = "",
    val orderPosition: Int = 0
)