package com.example.plugins

import io.ktor.http.ContentType
import com.example.models.adminRoutes
import com.example.routes.*
import com.example.services.*
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.response.respond
import io.ktor.server.routing.*

import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.http.HttpStatusCode
import io.github.cdimascio.dotenv.dotenv

fun Route.protectedRoute(callback: Route.() -> Unit) {
    val dotenv = dotenv { ignoreIfMissing = true }
    val devMode = dotenv["DEV_MODE"]?.toBoolean() ?: false

    if (devMode) {
        callback()
    } else {
        authenticate("auth-jwt") {
            callback()
        }
    }
}

fun Route.protectedAdminRoute(callback: Route.() -> Unit) {
    val dotenv = dotenv { ignoreIfMissing = true }
    val devMode = dotenv["DEV_MODE"]?.toBoolean() ?: false

    if (devMode) {
        callback()
    } else {
        authenticate("auth-jwt") {
            intercept(ApplicationCallPipeline.Call) {
                val principal = call.principal<io.ktor.server.auth.jwt.JWTPrincipal>()
                val isAdmin = principal?.payload?.getClaim("isAdmin")?.asBoolean() ?: false
                if (!isAdmin) {
                    call.respond(io.ktor.http.HttpStatusCode.Forbidden, mapOf("error" to "Admin access required"))
                    finish()
                }
            }
            callback()
        }
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Servidor funcionando ✅", ContentType.Text.Plain)
        }

        get("/api/status") {
            val dotenv = dotenv { ignoreIfMissing = true }
            val devMode = dotenv["DEV_MODE"]?.toBoolean() ?: false
            val mode = if (devMode) "DEV" else "PROD"
            call.respond(mapOf("status" to "ok", "mode" to mode))
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
