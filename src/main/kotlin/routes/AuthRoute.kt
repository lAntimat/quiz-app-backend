package com.quiz.routes

import UserRepository
import auth.AuthResponse
import auth.AuthenticationException
import auth.JwtConfig
import auth.LoginRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

fun Route.authController(userRepository: UserRepository) {
    post("/api/auth/register") {
        val request = call.receive<LoginRequest>()

        if (userRepository.findUserByUsername(request.username) != null) {
            call.respond(HttpStatusCode.Conflict, "Username already exists")
            return@post
        }

        val userId = userRepository.createUser(request.username, request.password)
        val token = JwtConfig.makeToken(userId)

        call.respond(AuthResponse(token))
    }

    post("/api/auth/login") {
        val request = call.receive<LoginRequest>()

        val user = userRepository.validateCredentials(request.username, request.password)
            ?: throw AuthenticationException("Invalid credentials")

        val token = JwtConfig.makeToken(user.id)
        call.respond(AuthResponse(token))
    }

    authenticate("auth-jwt") {
        get("/api/auth/me") {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("userId")?.asString()
            val user = userId?.let { userRepository.findUserById(it) }

            if (user != null) {
                call.respond(user)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}
