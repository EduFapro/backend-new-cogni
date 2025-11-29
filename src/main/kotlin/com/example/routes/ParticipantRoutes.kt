package com.example.routes

import com.example.models.Participant
import com.example.models.validateRequiredFields
import com.example.services.ParticipantService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Route.participantRoutes(service: ParticipantService) {
    val logger = LoggerFactory.getLogger("ParticipantRoutes")

    route("/api/participants") {
        post {
            try {
                val participant = call.receive<Participant>()
                
                val missingFields = participant.validateRequiredFields()
                if (missingFields.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Missing required fields", "missing" to missingFields)
                    )
                    return@post
                }

                val id = service.create(participant)
                call.respond(HttpStatusCode.Created, mapOf("id" to id, "message" to "Participant created successfully"))
            } catch (e: Exception) {
                logger.error("Error creating participant", e)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to create participant: ${e.message}"))
            }
        }

        get("/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@get
                }

                val participant = service.getById(id)
                if (participant != null) {
                    call.respond(HttpStatusCode.OK, participant)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Participant not found"))
                }
            } catch (e: Exception) {
                logger.error("Error getting participant", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        get {
            try {
                val evaluatorId = call.request.queryParameters["evaluatorId"]?.toIntOrNull()
                
                if (evaluatorId != null) {
                    val participants = service.getByEvaluatorId(evaluatorId)
                    call.respond(HttpStatusCode.OK, participants)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "evaluatorId query parameter required"))
                }
            } catch (e: Exception) {
                logger.error("Error getting participants", e)
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

                val participant = call.receive<Participant>()
                val updated = service.update(id, participant)
                
                if (updated > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Participant updated successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Participant not found"))
                }
            } catch (e: Exception) {
                logger.error("Error updating participant", e)
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
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Participant deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Participant not found"))
                }
            } catch (e: Exception) {
                logger.error("Error deleting participant", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}
