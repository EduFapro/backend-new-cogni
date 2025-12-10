package com.example.models.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object PasswordResetTokenTable : Table("password_reset_tokens") {
    val token = varchar("token", 255)
    val userId = integer("user_id").references(EvaluatorTable.id)
    val expiresAt = datetime("expires_at")

    override val primaryKey = PrimaryKey(token)
}
