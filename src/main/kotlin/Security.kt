package com.quiz

import auth.JwtConfig
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import userRepository

fun Application.configureSecurity() {
    authentication {
        jwt("auth-jwt") {
            realm = "ktor server"
            verifier(
                JWT.require(JwtConfig.algorithm)
                    .withIssuer(JwtConfig.issuer)
                    .build()
            )
            validate { credential ->
                val userId = credential.payload.getClaim("userId").asString()
                userRepository.findUserById(userId)?.let {
                    JWTPrincipal(credential.payload)
                }
            }
        }
    }


}
