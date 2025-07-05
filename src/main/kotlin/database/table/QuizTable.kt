package com.quiz.database.table

import org.jetbrains.exposed.dao.id.LongIdTable

// Таблицы
object QuizTable : LongIdTable("quizzes") {
    val title = varchar("title", 255)
    val createdAt = long("created_at")
    val difficulty = integer("difficulty")
}