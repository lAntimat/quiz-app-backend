package com.quiz.database.database

import kotlinx.serialization.Serializable

@Serializable
data class Quiz(
    val title: String,
    val createdAt: Long,
    val difficulty: Int,
)