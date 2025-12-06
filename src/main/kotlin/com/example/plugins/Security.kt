package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.github.cdimascio.dotenv.dotenv

fun Application.configureSecurity() {
    val dotenv = dotenv { ignoreIfMissing = true }
    val jwtSecret = dotenv["JWT_SECRET"] ?: "dev-secret-key"
    val jwtIssuer = dotenv["JWT_ISSUER"] ?: "http://0.0.0.0:8080/"
    val jwtAudience = dotenv["JWT_AUDIENCE"] ?: "http://0.0.0.0:8080/hello"
    val jwtRealm = dotenv["JWT_REALM"] ?: "Access to 'hello'"

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.audience.contains(jwtAudience)) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
