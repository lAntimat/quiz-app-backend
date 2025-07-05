package auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(val username: String, val password: String)

@Serializable
data class AuthResponse(val token: String)

class AuthenticationException(message: String) : RuntimeException(message)