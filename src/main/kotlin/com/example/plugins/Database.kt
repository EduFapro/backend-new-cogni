package com.example.plugins

import com.example.models.tables.*
import com.example.services.AuthService
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

fun Application.configureDatabase() {
    val dotenv = dotenv {
        ignoreIfMissing = true
    }

    val dbUrl = dotenv["DB_URL"] ?: error("DB_URL is missing in .env")
    val dbUser = dotenv["DB_USER"] ?: error("DB_USER is missing in .env")
    val dbPass = dotenv["DB_PASSWORD"] ?: error("DB_PASSWORD is missing in .env")

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
            RecordingFileTable
        )

        // Seeding
        if (EvaluatorTable.selectAll().count() == 0L) {
            val logger = LoggerFactory.getLogger("DatabaseSeeding")
            logger.info("🌱 Seeding default admin user...")
            
            val authService = AuthService()
            val hashedPassword = authService.hashPassword("admin")

            EvaluatorTable.insert {
                it[name] = "Admin"
                it[surname] = "User"
                it[email] = "admin@cogni.com"
                it[birthDate] = "2000-01-01"
                it[specialty] = "Administration"
                it[cpfOrNif] = "00000000000"
                it[username] = "admin"
                it[password] = hashedPassword
                it[isAdmin] = true
                it[firstLogin] = false
            }
            logger.info("✅ Default admin user created: admin / admin")
        }
    }
}
