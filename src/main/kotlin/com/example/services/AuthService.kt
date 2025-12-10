package com.example.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.models.tables.EvaluatorTable
import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class AuthService {
    private val dotenv = dotenv { ignoreIfMissing = true }
    private val jwtSecret = dotenv["JWT_SECRET"] ?: "dev-secret-key"
    private val jwtIssuer = dotenv["JWT_ISSUER"] ?: "http://0.0.0.0:8080/"
    private val jwtAudience = dotenv["JWT_AUDIENCE"] ?: "http://0.0.0.0:8080/hello"

    fun login(usernameOrEmail: String, password: String):String? {
        val user = transaction {
            EvaluatorTable.selectAll().where { (EvaluatorTable.username eq usernameOrEmail) or (EvaluatorTable.email eq usernameOrEmail) }
                .singleOrNull()
        }

        if (user == null) {
            return null
        }

        val storedUsername = user[EvaluatorTable.username]
        val storedEmail = user[EvaluatorTable.email]
        val hashedPassword = user[EvaluatorTable.password]
        
        if (!BCrypt.checkpw(password, hashedPassword)) {
            return null
        }

        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("username", user[EvaluatorTable.username])
            .withClaim("id", user[EvaluatorTable.id])
            .withClaim("isAdmin", user[EvaluatorTable.isAdmin])
            .withExpiresAt(Date(System.currentTimeMillis() + 60000 * 60 * 2)) // 2 hours
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun changePassword(userId: Int, oldPassword: String, newPassword: String): Boolean {
        val user = transaction {
            EvaluatorTable.select { EvaluatorTable.id eq userId }.singleOrNull()
        } ?: return false

        val currentHashedPassword = user[EvaluatorTable.password]
        if (!BCrypt.checkpw(oldPassword, currentHashedPassword)) {
            return false
        }

        val newHashed = hashPassword(newPassword)
        transaction {
            EvaluatorTable.update({ EvaluatorTable.id eq userId }) {
                it[password] = newHashed
            }
        }
        return true
    }

    fun requestPasswordReset(email: String): String? {
        val user = transaction {
            EvaluatorTable.select { EvaluatorTable.email eq email }.singleOrNull()
        } ?: return null

        val userId = user[EvaluatorTable.id]
        val token = UUID.randomUUID().toString()

        transaction {
            // Invalidate old tokens for this user
            com.example.models.tables.PasswordResetTokenTable.deleteWhere {
                com.example.models.tables.PasswordResetTokenTable.userId eq userId
            }

            com.example.models.tables.PasswordResetTokenTable.insert {
                it[com.example.models.tables.PasswordResetTokenTable.token] = token
                it[com.example.models.tables.PasswordResetTokenTable.userId] = userId
                it[expiresAt] = java.time.LocalDateTime.now().plusMinutes(15)
            }
        }
        
        // In a real app, send email here. For now, return token for logging/testing.
        println("RESET TOKEN FOR $email: $token")
        return token
    }

    fun resetPassword(token: String, newPassword: String): Boolean {
        val tokenRow = transaction {
            com.example.models.tables.PasswordResetTokenTable.select {
                com.example.models.tables.PasswordResetTokenTable.token eq token
            }.singleOrNull()
        } ?: return false

        val expiresAt = tokenRow[com.example.models.tables.PasswordResetTokenTable.expiresAt]
        if (expiresAt.isBefore(java.time.LocalDateTime.now())) {
            return false
        }

        val userId = tokenRow[com.example.models.tables.PasswordResetTokenTable.userId]
        val newHashed = hashPassword(newPassword)

        transaction {
            EvaluatorTable.update({ EvaluatorTable.id eq userId }) {
                it[password] = newHashed
            }
            // Invalidate token
            com.example.models.tables.PasswordResetTokenTable.deleteWhere {
                com.example.models.tables.PasswordResetTokenTable.token eq token
            }
        }
        return true
    }
}
