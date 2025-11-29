package com.example.routes

import com.example.models.ModuleInstance
import com.example.models.validateRequiredFields
import com.example.services.ModuleInstanceService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

@Serializable
data class ModuleStatusUpdate(val status: Int)

fun Route.moduleInstanceRoutes(service: ModuleInstanceService) {
    val logger = LoggerFactory.getLogger("ModuleInstanceRoutes")

    route("/api/module-instances") {
        post {
            try {
                val moduleInstance = call.receive<ModuleInstance>()
                
                val missingFields = moduleInstance.validateRequiredFields()
                if (missingFields.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Missing required fields", "missing" to missingFields)
                    )
                    return@post
                }

                val id = service.create(moduleInstance)
                call.respond(HttpStatusCode.Created, mapOf("id" to id, "message" to "Module instance created successfully"))
            } catch (e: Exception) {
                logger.error("Error creating module instance", e)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to create module instance: ${e.message}"))
            }
        }

        get("/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@get
                }

                val moduleInstance = service.getById(id)
                if (moduleInstance != null) {
                    call.respond(HttpStatusCode.OK, moduleInstance)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Module instance not found"))
                }
            } catch (e: Exception) {
                logger.error("Error getting module instance", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        get {
            try {
                val evaluationId = call.request.queryParameters["evaluationId"]?.toIntOrNull()
                
                if (evaluationId != null) {
                    val instances = service.getByEvaluationId(evaluationId)
                    call.respond(HttpStatusCode.OK, instances)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "evaluationId query parameter required"))
                }
            } catch (e: Exception) {
                logger.error("Error getting module instances", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        patch("/{id}/status") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@patch
                }

                val statusUpdate = call.receive<ModuleStatusUpdate>()
                if (statusUpdate.status !in 1..3) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Status must be 1 (Pending), 2 (InProgress), or 3 (Completed)"))
                    return@patch
                }

                val updated = service.updateStatus(id, statusUpdate.status)
                if (updated > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Status updated successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Module instance not found"))
                }
            } catch (e: Exception) {
                logger.error("Error updating module instance status", e)
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
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Module instance deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Module instance not found"))
                }
            } catch (e: Exception) {
                logger.error("Error deleting module instance", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}
