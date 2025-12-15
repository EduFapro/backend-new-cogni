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

    // 1. Prefer DATABASE_URL from Railway (canonical internal)
    // We must manually parse it because adding "jdbc:" to "postgresql://user:pass@host"
    // often causes UnknownHostException with some JDBC drivers which don't expect auth in the Authority info.
    val rawUrl = System.getenv("DATABASE_URL")
    
    // Variables to be populated
    var finalUrl = ""
    var finalUser = ""
    var finalPass = ""

    if (!rawUrl.isNullOrBlank()) {
        try {
            // Remove jdbc: prefix if present for parsing (though usually it's just postgres://...)
            val cleanUrl = if (rawUrl.startsWith("jdbc:")) rawUrl.substring(5) else rawUrl
            val uri = java.net.URI(cleanUrl)
            
            val userPass = uri.userInfo?.split(":")
            val user = userPass?.getOrNull(0) ?: ""
            val pass = userPass?.getOrNull(1) ?: ""
            val host = uri.host
            val port = if (uri.port == -1) 5432 else uri.port
            val path = uri.path // includes leading /
            
            // Reconstruct standard JDBC URL without credentials
            finalUrl = "jdbc:postgresql://$host:$port$path"
            finalUser = user
            finalPass = pass
            
            val logger = LoggerFactory.getLogger("DatabaseConfig")
            logger.info("🔌 Parsed DATABASE_URL: Host=$host Port=$port User=${if(user.isNotEmpty()) "SET" else "NONE"}")
            
        } catch (e: Exception) {
            LoggerFactory.getLogger("DatabaseConfig").warn("⚠️ Failed to parse DATABASE_URL, falling back to direct string: ${e.message}")
            finalUrl = if (rawUrl.startsWith("jdbc:")) rawUrl else "jdbc:$rawUrl"
        }
    }

    // 2. Fallback to decomposed Env Vars (DB_URL, DB_USER, etc)
    if (finalUrl.isEmpty()) {
        finalUrl = System.getenv("DB_URL") ?: dotenv["DB_URL"] ?: error("No valid DB_URL or DATABASE_URL found")
        finalUser = System.getenv("DB_USER") ?: dotenv["DB_USER"] ?: error("DB_USER is missing")
        finalPass = System.getenv("DB_PASSWORD") ?: dotenv["DB_PASSWORD"] ?: error("DB_PASSWORD is missing")
    } else {
        // parsing succeeded, but we might want to allow overrides if specifically set (rare)
        // If extracted user/pass is empty, try to fill from specific env vars
        if (finalUser.isEmpty()) finalUser = System.getenv("DB_USER") ?: dotenv["DB_USER"] ?: ""
        if (finalPass.isEmpty()) finalPass = System.getenv("DB_PASSWORD") ?: dotenv["DB_PASSWORD"] ?: ""
    }

    val logger = LoggerFactory.getLogger("DatabaseConfig")
    try {
        val host = finalUrl.substringAfter("://").substringBefore(":").substringBefore("/")
        // logger.info("🔌 Database Host: $host") // redundant if parsed above, but keeps consistency
    } catch (e: Exception) {}

    // Connect
    Database.connect(
        url = finalUrl,
        driver = "org.postgresql.Driver",
        user = finalUser,
        password = finalPass
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
