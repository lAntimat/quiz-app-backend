package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import java.util.*

object JwtConfig {
    private const val secret = "your-secret-key" // В production используйте сложный ключ из конфига
    const val issuer = "your-issuer"
    private const val validityInMs = 36_000_00 * 24 // 24 часа
    
    val algorithm = Algorithm.HMAC256(secret)
    
    fun makeToken(userId: String): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(issuer)
            .withClaim("userId", userId)
            .withExpiresAt(getExpiration())
            .sign(algorithm)
    }
    
    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)
}