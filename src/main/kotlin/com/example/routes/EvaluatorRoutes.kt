package com.example.routes

import com.example.models.Evaluator
import com.example.services.EvaluatorService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

import kotlinx.serialization.json.*

import com.example.services.AuthService
import com.example.plugins.protectedRoute
import kotlinx.serialization.Serializable

@Serializable
data class LoginDTO(val email: String, val password: String)

fun Route.evaluatorRoutes(service: EvaluatorService) {
    val logger = LoggerFactory.getLogger("EvaluatorRoutes")
    val authService = AuthService()

    route("/api/evaluators") {
        // Public Login Endpoint
        post("/login") {
            val loginRequest = try {
                call.receive<LoginDTO>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid JSON"))
                return@post
            }

            val token = authService.login(loginRequest.email, loginRequest.password)
            if (token != null) {
                call.respond(HttpStatusCode.OK, mapOf("token" to token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid credentials"))
            }
        }

        // Protected Endpoints
        protectedRoute {
            post {
                try {
                    val evaluator = call.receive<Evaluator>()
                    val id = service.create(evaluator)
                    call.respond(HttpStatusCode.Created, buildJsonObject {
                        put("id", id)
                        put("message", "Evaluator created successfully")
                    })
                } catch (e: Exception) {
                    logger.error("Error creating evaluator", e)
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Failed to create evaluator: ${e.message}")
                    )
                }
            }

            get("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                        return@get
                    }

                    val evaluator = service.getById(id)
                    if (evaluator != null) {
                        call.respond(HttpStatusCode.OK, evaluator)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Evaluator not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Error getting evaluator", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }

            put("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                        return@put
                    }

                    val evaluator = call.receive<Evaluator>()
                    val updated = service.update(id, evaluator)
                    
                    if (updated > 0) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Evaluator updated successfully"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Evaluator not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Error updating evaluator", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }

            delete("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                        return@delete
                    }

                    val deleted = service.delete(id)
                    if (deleted > 0) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Evaluator deleted successfully"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Evaluator not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Error deleting evaluator", e)
                    call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
                }
            }
        }
    }
}
