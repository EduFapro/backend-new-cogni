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

    // 1. Prefer DATABASE_URL from Railway (canonical internal), prefixing with jdbc: if needed
    // 2. Fallback to DB_URL from env or .env
    val rawUrl = System.getenv("DATABASE_URL")
    val dbUrl = when {
        rawUrl != null && !rawUrl.startsWith("jdbc:") -> "jdbc:$rawUrl"
        rawUrl != null -> rawUrl
        else -> System.getenv("DB_URL") ?: dotenv["DB_URL"] ?: error("No valid DB_URL or DATABASE_URL found")
    }

    val dbUser = System.getenv("DB_USER") ?: dotenv["DB_USER"] ?: error("DB_USER is missing")
    val dbPass = System.getenv("DB_PASSWORD") ?: dotenv["DB_PASSWORD"] ?: error("DB_PASSWORD is missing")

    val logger = LoggerFactory.getLogger("DatabaseConfig")
    try {
        // Robust parsing to check for private vs public host
        val host = dbUrl.substringAfter("://").substringBefore(":").substringBefore("/")
        logger.info("🔌 Database Host: $host")
        
        if (host.contains("proxy.rlwy.net", ignoreCase = true)) {
             logger.warn("⚠️ WARNING: DB host looks like a public TCP proxy ($host). You might incur egress fees! verify configuration.")
        }
    } catch (e: Exception) {
        logger.warn("Could not parse DB Host for verification")
    }

    // Connect
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


        // Seeding Demo User (Protected by Env Flag)
        if (System.getenv("SEED_DEMO") == "true") {
            val authService = AuthService()
            val demoPassword = authService.hashPassword("0000") // Keep source simple, but gate execution
            
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
                    logger.info("✅ Demo user created.")
                } catch (e: Exception) {
                    logger.error("❌ Failed to seed demo user: ${e.message}")
                }
            } else {
                 // Force update password - optional, maybe only if specifically requested?
                 // For safety in prod, let's NOT force reset existing demo users unless explicitly handled.
                 // If you really need to reset, you can drop the user or DB.
                 logger.info("ℹ️ Demo user exists. Skipping re-seed.")
            }
        }
    }
}
