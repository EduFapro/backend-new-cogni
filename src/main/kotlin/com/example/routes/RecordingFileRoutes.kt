package com.example.routes

import com.example.models.RecordingFile
import com.example.models.validateRequiredFields
import com.example.services.RecordingFileService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

fun Route.recordingFileRoutes(service: RecordingFileService) {
    val logger = LoggerFactory.getLogger("RecordingFileRoutes")

    route("/api/recordings") {
        post {
            try {
                val recordingFile = call.receive<RecordingFile>()
                
                val missingFields = recordingFile.validateRequiredFields()
                if (missingFields.isNotEmpty()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Missing required fields", "missing" to missingFields)
                    )
                    return@post
                }

                val id = service.create(recordingFile)
                call.respond(HttpStatusCode.Created, mapOf("id" to id, "message" to "Recording file created successfully"))
            } catch (e: Exception) {
                logger.error("Error creating recording file", e)
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Failed to create recording file: ${e.message}"))
            }
        }

        get("/{id}") {
            try {
                val id = call.parameters["id"]?.toIntOrNull()
                if (id == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid ID"))
                    return@get
                }

                val recordingFile = service.getById(id)
                if (recordingFile != null) {
                    call.respond(HttpStatusCode.OK, recordingFile)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Recording file not found"))
                }
            } catch (e: Exception) {
                logger.error("Error getting recording file", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }

        get("/metadata/{taskInstanceId}") {
            try {
                val taskInstanceId = call.parameters["taskInstanceId"]?.toIntOrNull()
                if (taskInstanceId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid task instance ID"))
                    return@get
                }

                val recordingFile = service.getByTaskInstanceId(taskInstanceId)
                if (recordingFile != null) {
                    call.respond(HttpStatusCode.OK, recordingFile)
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Recording file not found"))
                }
            } catch (e: Exception) {
                logger.error("Error getting recording file by task instance", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
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
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Recording file deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to "Recording file not found"))
                }
            } catch (e: Exception) {
                logger.error("Error deleting recording file", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
            }
        }
    }
}
