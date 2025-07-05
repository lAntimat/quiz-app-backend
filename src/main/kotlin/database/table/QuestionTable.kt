package com.quiz.database.table

import com.quiz.database.table.QuizTable
import org.jetbrains.exposed.dao.id.LongIdTable

object QuestionTable : LongIdTable("questions") {
    val quizId = long("quiz_id").references(QuizTable.id)
    val title = varchar("title", 255)
    val variants = text("variants")  // Храним JSON как текст
    val answerKey = integer("answer_key")
    val imageUrl = varchar("image_url", 512).nullable()
    val hint = varchar("hint", 512)
    val orderPosition = integer("order_position")
}