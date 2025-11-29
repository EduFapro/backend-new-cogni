package com.example.plugins

import io.ktor.http.ContentType
import com.example.models.adminRoutes
import com.example.routes.*
import com.example.services.*
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Servidor funcionando ✅", ContentType.Text.Plain)
        }
        
        // Admin routes (existing)
        adminRoutes(AdminService())
        
        // New API routes
        evaluatorRoutes(EvaluatorService())
        participantRoutes(ParticipantService())
        evaluationRoutes(EvaluationService())
        moduleInstanceRoutes(ModuleInstanceService())
        taskInstanceRoutes(TaskInstanceService())
        recordingFileRoutes(RecordingFileService())
    }
}
