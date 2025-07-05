import at.favre.lib.crypto.bcrypt.BCrypt
import com.quiz.database.table.Users
import com.quiz.repository.dbQuery
import org.jetbrains.exposed.sql.*
import java.time.Instant
import java.util.UUID

val userRepository = UserRepository()

class UserRepository() {

    suspend fun createUser(username: String, password: String): String = dbQuery {
        val passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray())

        return@dbQuery Users.insert {
            it[Users.username] = username
            it[Users.passwordHash] = passwordHash
            it[Users.createdAt] = Instant.now()
        }[Users.id].value.toString()
    }

    suspend fun findUserById(id: String): User? = dbQuery {
        return@dbQuery Users.select { Users.id eq UUID.fromString(id) }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    suspend fun findUserByUsername(username: String): User? = dbQuery {
        return@dbQuery Users.select { Users.username eq username }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    suspend fun validateCredentials(username: String, password: String): User? {
        val user = findUserByUsername(username) ?: return null
        val result = BCrypt.verifyer().verify(password.toCharArray(), user.passwordHash)
        return if (result.verified) user else null
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[Users.id].value.toString(),
            username = row[Users.username],
            passwordHash = row[Users.passwordHash],
            createdAt = row[Users.createdAt]
        )
    }
}

data class User(
    val id: String,
    val username: String,
    val passwordHash: String,
    val createdAt: java.time.Instant
)