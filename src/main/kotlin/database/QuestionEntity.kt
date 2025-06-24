package com.quiz.database

import com.quiz.repository.jsonFormat
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class QuestionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<QuestionEntity>(QuestionTable)

    var quizId by QuestionTable.quizId
    var title by QuestionTable.title
    var variantsJson by QuestionTable.variants  // Сырой JSON
    var answerKey by QuestionTable.answerKey
    var imageUrl by QuestionTable.imageUrl
    var hint by QuestionTable.hint
    var orderPosition by QuestionTable.orderPosition

    // Преобразование между List<String> и JSON
    var variants: List<String>
        get() = jsonFormat.decodeFromString(variantsJson)
        set(value) { variantsJson = jsonFormat.encodeToString(value) }

    fun toResponse() = QuestionResponse(
        id.value,
        quizId,
        title,
        variants,
        answerKey,
        imageUrl,
        hint,
        orderPosition
    )
}