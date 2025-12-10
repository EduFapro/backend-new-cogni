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

        val logger = LoggerFactory.getLogger("DatabaseSeeding")

//      // Seeding Admin
//      if (EvaluatorTable.selectAll().none { it[EvaluatorTable.username] == "admin" }) {
//          logger.info("🌱 Seeding default admin user...")
//          
//          val authService = AuthService()
//          val hashedPassword = authService.hashPassword("admin")
//
//          EvaluatorTable.insert {
//              it[name] = "Admin"
//              it[surname] = "User"
//              it[email] = "admin@cogni.com"
//              it[birthDate] = "2000-01-01"
//              it[specialty] = "Administration"
//              it[cpfOrNif] = "00000000000"
//              it[username] = "admin"
//              it[password] = hashedPassword
//              it[isAdmin] = true
//              it[firstLogin] = false
//          }
//          logger.info("✅ Default admin user created: admin / admin")
//      }

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
