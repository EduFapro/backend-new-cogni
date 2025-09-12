package com.example.models

import com.example.services.AdminService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory


fun Route.adminRoutes(service: AdminService) {
    val logger = LoggerFactory.getLogger("AdminRoutes")

    route("/admin") {
        post("/register") {
            val raw = call.receiveText()
            println("RAW recebido: $raw")

            val admin = Json { ignoreUnknownKeys = true }
                .decodeFromString<Admin>(raw)
            println("Desserializado manual: $admin")


            println("📥 Body recebido: $admin")
            logger.info("Novo admin recebido: {}", admin)
            call.respond(HttpStatusCode.OK, "Admin recebido com sucesso ✅")
        }

    }
}
