import com.quiz.database.table.QuestionTable
import com.quiz.database.QuizResponse
import com.quiz.database.table.QuizTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class QuizEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<QuizEntity>(QuizTable) {

    }

    var title by QuizTable.title
    var createdAt by QuizTable.createdAt
    var difficulty by QuizTable.difficulty

    // Ленивая загрузка вопросов
    val questions by QuestionEntity referrersOn QuestionTable.quizId

    fun toResponse(): QuizResponse {
        return QuizResponse(
            quizId = id.value,
            title = title,
            createdAt = createdAt,
            difficulty = difficulty,
            questionCount = 0,
            questions = listOf()
        )
    }
}