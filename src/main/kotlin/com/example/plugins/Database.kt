package com.example.plugins

import com.example.models.tables.*
import com.example.services.AuthService
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

fun Application.configureDatabase() {
    val dotenv = dotenv {
        ignoreIfMissing = true
    }

    val dbUrl = System.getenv("DB_URL") ?: dotenv["DB_URL"] ?: error("DB_URL is missing in .env")
    val dbUser = System.getenv("DB_USER") ?: dotenv["DB_USER"] ?: error("DB_USER is missing in .env")
    val dbPass = System.getenv("DB_PASSWORD") ?: dotenv["DB_PASSWORD"] ?: error("DB_PASSWORD is missing in .env")

    val logger = LoggerFactory.getLogger("DatabaseConfig")
    try {
        // Naive parse to log host for verification (jdbc:postgresql://HOST:PORT/DB)
        val host = dbUrl.substringAfter("://").substringBefore(":")
        logger.info("🔌 Database Host: $host (Check if internal/private)")
    } catch (e: Exception) {
        logger.warn("Could not parse DB Host from URL")
    }

    Database.connect(
        url = dbUrl,
        driver = "org.postgresql.Driver",
        user = dbUser,
        password = dbPass
    )

    // Create tables if they don't exist
    transaction {
        SchemaUtils.create(
            EvaluatorTable,
            ParticipantTable,
            EvaluationTable,
            ModuleInstanceTable,
            TaskInstanceTable,
            RecordingFileTable,
            PasswordResetTokenTable
        )

        val logger = LoggerFactory.getLogger("DatabaseSeeding")


        // Seeding Demo User
        val authService = AuthService()
        val demoPassword = authService.hashPassword("0000")
        
        if (EvaluatorTable.selectAll().none { it[EvaluatorTable.username] == "demo" }) {
            logger.info("🌱 Seeding demo user...")

            try {
                EvaluatorTable.insert {
                    it[name] = "Demo"
                    it[surname] = "User"
                    it[email] = "demo@example.com"
                    it[birthDate] = "1998-07-14"
                    it[specialty] = "Psicologia"
                    it[cpfOrNif] = "03240120010"
                    it[username] = "demo"
                    it[password] = demoPassword
                    it[isAdmin] = false
                    it[firstLogin] = true
                }
                logger.info("✅ Demo user created: demo@example.com / 0000")
            } catch (e: Exception) {
                logger.error("❌ Failed to seed demo user: ${e.message}")
            }
        } else {
             // Force update password to ensure it matches "0000"
             try {
                 EvaluatorTable.update({ EvaluatorTable.username eq "demo" }) {
                    it[password] = demoPassword
                 }
                 logger.info("🔄 Demo user password reset to: 0000")
             } catch (e: Exception) {
                 logger.error("❌ Failed to update demo user password: ${e.message}")
             }
        }
    }
}
