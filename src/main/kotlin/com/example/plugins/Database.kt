package com.example.plugins

import com.example.models.tables.*
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val dotenv = dotenv {
        ignoreIfMissing = false
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
    }
}
