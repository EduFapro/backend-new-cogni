package com.example.models.tables

import org.jetbrains.exposed.sql.Table
import java.time.LocalDateTime

object PasswordResetTokenTable : Table("password_reset_tokens") {
    val token = varchar("token", 255)
    val userId = varchar("user_id", 36).references(EvaluatorTable.id)
    val expiresAt = varchar("expires_at", 25)

    override val primaryKey = PrimaryKey(token)
}
