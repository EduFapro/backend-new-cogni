package com.example.models

import com.example.services.AdminService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory
import com.example.plugins.protectedAdminRoute

fun Route.adminRoutes(service: AdminService) {
    val logger = LoggerFactory.getLogger("AdminRoutes")

    protectedAdminRoute {
        route("/admin") {
            post("/register") {
                val admin = try {
                    call.receive<AdminDTO>()
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Invalid JSON: ${e.message}")
                    )
                    return@post
                }

                val missingFields = admin.validateRequiredFields()
                if (missingFields.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf(
                            "error" to "Missing required fields",
                            "missing" to missingFields
                        )
                    )
                    return@post
                }

                logger.info("Novo admin recebido: {}", admin)

                // ✅ If everything is valid, delegate to service
                service.register(admin)

                call.respond(
                    HttpStatusCode.OK,
                    mapOf("message" to "Admin registrado com sucesso ✅")
                )
            }
        }
    }
}
