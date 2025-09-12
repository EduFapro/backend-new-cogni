package com.example.plugins
import io.ktor.http.ContentType
import com.example.models.adminRoutes
import com.example.services.AdminService
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Servidor funcionando ✅", ContentType.Text.Plain)
        }
        adminRoutes(AdminService())
    }
}
