package com.quiz.repository.models

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class OpenApiRequest(
    val model: String,
    val messages: List<OpenApiMessage>,
    @EncodeDefault val temperature: Double = 0.7
)

@Serializable
data class OpenApiMessage(
    val role: String,
    val content: String,
)