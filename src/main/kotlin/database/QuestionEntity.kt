import com.quiz.database.QuestionResponse
import com.quiz.database.table.QuestionTable
import com.quiz.repository.jsonFormat
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class QuestionEntity(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<QuestionEntity>(QuestionTable) {
        // Пакетная загрузка вопросов для викторины
        fun findForQuiz(quizId: Long): List<QuestionEntity> {
            return transaction {
                find { QuestionTable.quizId eq quizId }
                    .sortedBy { it.orderPosition }
                    .toList()
            }
        }
    }

    var quizId by QuestionTable.quizId
    var title by QuestionTable.title
    var variantsJson by QuestionTable.variants
    var answerKey by QuestionTable.answerKey
    var imageUrl by QuestionTable.imageUrl
    var hint by QuestionTable.hint
    var orderPosition by QuestionTable.orderPosition

    // Преобразование JSON
    var variants: List<String>
        get() = jsonFormat.decodeFromString(variantsJson)
        set(value) { variantsJson = jsonFormat.encodeToString(value) }

    fun toResponse(): QuestionResponse {
        return QuestionResponse(
            id = id.value,
            quizId = quizId,
            title = title,
            variants = variants,
            answerKey = answerKey,
            imageUrl = imageUrl,
            hint = hint,
            orderPosition = orderPosition
        )
    }
}