package com.quiz.repository

import com.quiz.database.QuestionResponse
import com.quiz.database.table.QuestionTable
import com.quiz.database.QuizResponse
import com.quiz.database.table.QuizTable
import com.quiz.database.database.Quiz
import org.jetbrains.exposed.sql.SortOrder

class QuizRepository {
    suspend fun insertQuiz(item: Quiz) = dbQuery {
        QuizEntity.new {
            title = item.title
            createdAt = item.createdAt
            difficulty = item.difficulty
        }.id.value
    }

    suspend fun updateQuiz(item: QuizResponse) = dbQuery {
        QuizEntity[item.quizId].apply {
            title = item.title
            createdAt = item.createdAt
            difficulty = item.difficulty
        }
    }

    suspend fun deleteQuiz(id: Long) = dbQuery {
        QuizEntity.findById(id)?.delete()
    }

    suspend fun getQuizById(quizId: Long) = dbQuery {
        QuizEntity.findById(quizId)?.toResponse()
    }

    suspend fun getAllQuizzes() = dbQuery {
        QuizEntity.all().orderBy(QuizTable.createdAt to SortOrder.DESC)
            .map { it.toResponse() }
    }

    // Question operations
    suspend fun insertQuestion(item: QuestionResponse, quizId: Long) = dbQuery {
        QuestionEntity.new {
            this.quizId = quizId
            title = item.title
            variants = item.variants  // Автоматически конвертируется в JSON
            answerKey = item.answerKey
            imageUrl = item.imageUrl
            hint = item.hint
            orderPosition = item.orderPosition ?: 0
        }.id.value
    }

    suspend fun updateQuestion(item: QuestionResponse, quizId: Long) = dbQuery {
        QuestionEntity[item.id].apply {
            this.quizId = quizId
            title = item.title
            variants = item.variants
            answerKey = item.answerKey
            imageUrl = item.imageUrl
            hint = item.hint
            orderPosition = item.orderPosition ?: 0
        }
    }

    suspend fun deleteQuestion(questionId: Long) = dbQuery {
        QuestionEntity.findById(questionId)?.delete()
    }

    suspend fun getQuestion(id: Long) = dbQuery {
        QuestionEntity.findById(id)?.toResponse()
    }

    suspend fun getQuestionsByQuizId(quizId: Long) = dbQuery {
        QuestionEntity.find { QuestionTable.quizId eq quizId }
            .orderBy(QuestionTable.orderPosition to SortOrder.ASC)
            .map { it.toResponse() }
    }
}