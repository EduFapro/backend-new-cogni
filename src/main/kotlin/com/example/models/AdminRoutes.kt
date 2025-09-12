package com.example.models

import com.example.services.AdminService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminRoutes(service: AdminService) {
    route("/admin") {
        post("/register") {
            val admin = call.receive<AdminDTO>()
            val created = service.register(admin)
            call.respond(HttpStatusCode.Created, created)
        }
    }
}
