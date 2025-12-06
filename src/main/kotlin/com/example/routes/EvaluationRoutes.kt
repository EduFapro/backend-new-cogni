package com.example.routes

import com.example.models.Evaluation
import com.example.models.validateRequiredFields
import com.example.services.EvaluationService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import com.example.plugins.protectedRoute
import org.slf4j.LoggerFactory

import kotlinx.serialization.json.*

@Serializable
data class StatusUpdate(val status: Int)

fun Route.evaluationRoutes(service: EvaluationService) {
    val logger = LoggerFactory.getLogger("EvaluationRoutes")

    protectedRoute {
        route("/api/evaluations") {
            post {
                try {
                    val evaluation = call.receive<Evaluation>()
                    
                    val missingFields = evaluation.validateRequiredFields()
                    if (missingFields.isNotEmpty()) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to "Missing required fields", "missing" to missingFields)
                        )
                        return@post
                    }

                    val id = service.create(evaluation)
                    call.respond(HttpStatusCode.Created, buildJsonObject {
                        put("id", id)
                        put("message", "Evaluation created successfully")
                    })
                } catch (e: Exception) {
                    logger.error("Error creating evaluation", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to create evaluation: ${e.message}"))
                }
            }

            get("/{id}") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                        return@get
                    }

                    val evaluation = service.getById(id)
                    if (evaluation != null) {
                        call.respond(HttpStatusCode.OK, evaluation)
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Evaluation not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Error getting evaluation", e)
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

                    val evaluation = call.receive<Evaluation>()
                    val updated = service.update(id, evaluation)
                    
                    if (updated > 0) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Evaluation updated successfully"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Evaluation not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Error updating evaluation", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }

            patch("/{id}/status") {
                try {
                    val id = call.parameters["id"]?.toIntOrNull()
                    if (id == null) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                        return@patch
                    }

                    val statusUpdate = call.receive<StatusUpdate>()
                    if (statusUpdate.status !in 1..3) {
                        call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Status must be 1 (Pending), 2 (InProgress), or 3 (Completed)"))
                        return@patch
                    }

                    val updated = service.updateStatus(id, statusUpdate.status)
                    if (updated > 0) {
                        call.respond(HttpStatusCode.OK, mapOf("message" to "Status updated successfully"))
                    } else {
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to "Evaluation not found"))
                    }
                } catch (e: Exception) {
                    logger.error("Error updating evaluation status", e)
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
                }
            }
        }
    }
}
