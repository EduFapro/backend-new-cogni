package com.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.tables.EvaluatorTable
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class AuthService {
    private val dotenv = dotenv { ignoreIfMissing = true }
    private val jwtSecret = dotenv["JWT_SECRET"] ?: "dev-secret-key"
    private val jwtIssuer = dotenv["JWT_ISSUER"] ?: "http://0.0.0.0:8080/"
    private val jwtAudience = dotenv["JWT_AUDIENCE"] ?: "http://0.0.0.0:8080/hello"

    fun login(username: String, password: String):String? {
        val user = transaction {
            EvaluatorTable.select(EvaluatorTable.username eq username)
                .singleOrNull()
        } ?: return null

        val hashedPassword = user[EvaluatorTable.password]
        
        if (!BCrypt.checkpw(password, hashedPassword)) {
            return null
        }

        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("username", username)
            .withClaim("id", user[EvaluatorTable.id])
            .withClaim("isAdmin", user[EvaluatorTable.isAdmin])
            .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 60 * 2)) // 2 hours
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }
}
