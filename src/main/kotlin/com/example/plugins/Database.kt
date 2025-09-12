package com.example.plugins

import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

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
}
