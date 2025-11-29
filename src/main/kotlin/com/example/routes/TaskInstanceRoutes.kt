package com.example.routes

import com.example.models.TaskInstance
import com.example.models.validateRequiredFields
import com.example.services.TaskInstanceService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

@Serializable
data class TaskCompletionUpdate(val duration: String? = null)

fun Route.taskInstanceRoutes(service: TaskInstanceService) {
    val logger = LoggerFactory.getLogger("TaskInstanceRoutes")

    route("/api/task-instances") {
        post {
            try {
                val taskInstance = call.receive<TaskInstance>()
                
                val missingFields = taskInstance.validateRequiredFields()
                if (missingFields.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Missing required fields", "missing" to missingFields)
                    )
                    return@post
                }

                val id = service.create(taskInstance)
                call.respond(HttpStatusCode.Created, mapOf("id" to id, "message" to "Task instance created successfully"))
            } catch (e: Exception) {
                logger.error("Error creating task instance", e)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to create task instance: ${e.message}"))
            }
        }

        get("/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@get
                }

                val taskInstance = service.getById(id)
                if (taskInstance != null) {
                    call.respond(HttpStatusCode.OK, taskInstance)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task instance not found"))
                }
            } catch (e: Exception) {
                logger.error("Error getting task instance", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        get {
            try {
                val moduleInstanceId = call.request.queryParameters["moduleInstanceId"]?.toIntOrNull()
                
                if (moduleInstanceId != null) {
                    val instances = service.getByModuleInstanceId(moduleInstanceId)
                    call.respond(HttpStatusCode.OK, instances)
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "moduleInstanceId query parameter required"))
                }
            } catch (e: Exception) {
                logger.error("Error getting task instances", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        patch("/{id}/complete") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@patch
                }

                val completionUpdate = call.receive<TaskCompletionUpdate>()
                val updated = service.markAsCompleted(id, completionUpdate.duration)
                
                if (updated > 0) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Task marked as completed"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task instance not found"))
                }
            } catch (e: Exception) {
                logger.error("Error completing task instance", e)
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
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Task instance deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Task instance not found"))
                }
            } catch (e: Exception) {
                logger.error("Error deleting task instance", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}
