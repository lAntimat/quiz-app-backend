package com.quiz.repository

import com.quiz.database.table.QuestionTable
import com.quiz.database.table.QuizTable
import com.quiz.database.table.Users
import io.ktor.server.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

// Настройка JSON сериализатора
val jsonFormat = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}

fun Application.configureDatabase() {
    initDatabase()
}

private val DATABASE_URL = "postgresql://dpg-d1cn0qndiees73cfv1c0-a.oregon-postgres.render.com/quiz_database_for8"

// Инициализация базы данных
private fun initDatabase() {

    Database.connect(
        url = "jdbc:$DATABASE_URL",
        driver = "org.postgresql.Driver",
        user = "quiz_database_for8_user",
        password = "HtFnQFUuHza9hO7NXPlhSvgbbkfgpXk5"
    )

    transaction {
        SchemaUtils.create(QuizTable, QuestionTable)
        SchemaUtils.create(Users)
    }
}

// Вспомогательная функция для корутин
suspend fun <T> dbQuery(block: () -> T): T = withContext(Dispatchers.IO) {
    transaction { block() }
}