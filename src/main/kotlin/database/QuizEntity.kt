package com.quiz.database

import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

// Сущности
class QuizEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<QuizEntity>(QuizTable)

    var title by QuizTable.title
    var createdAt by QuizTable.createdAt
    var difficulty by QuizTable.difficulty

    val questions by QuestionEntity referrersOn QuestionTable.quizId

    fun toResponse() = QuizResponse(
        id.value,
        title,
        createdAt,
        difficulty
    )
}