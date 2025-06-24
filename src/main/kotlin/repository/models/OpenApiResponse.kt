package com.quiz.repository.models

import kotlinx.serialization.Serializable

@Serializable
data class OpenApiResponse(
    val id: String,
    val provider: String,
    val model: String,
    val `object`: String,  // object - зарезервированное слово, поэтому в кавычках
    val created: Long,
    val choices: List<Choice>,
    val usage: Usage?
)

@Serializable
data class Choice(
    val logprobs: String? = null,
    val finish_reason: String,
    val native_finish_reason: String,
    val index: Int,
    val message: Message,
    val refusal: String? = null,
    val reasoning: String? = null
)

@Serializable
data class Message(
    val role: String,
    val content: String  // Содержит вложенный JSON-текст
)

@Serializable
data class Usage(
    val prompt_tokens: Int,
    val completion_tokens: Int,
    val total_tokens: Int
)