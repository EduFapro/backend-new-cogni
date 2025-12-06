package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.plugins.ratelimit.*
// import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import io.ktor.server.request.path
import io.ktor.server.response.*
import io.ktor.http.*
import org.slf4j.LoggerFactory
import kotlin.time.Duration.Companion.seconds

fun Application.configureMonitoring() {
    val logger = LoggerFactory.getLogger("Monitoring")

    install(StatusPages) {
        exception<Throwable> { call, cause ->
            logger.error("Unhandled exception caught: ${cause.message}", cause)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to "Internal Server Error"))
        }

        exception<io.ktor.server.plugins.BadRequestException> { call, cause ->
            // Extract more meaningful message if possible
            val message = cause.cause?.message ?: cause.message ?: "Bad Request"
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to message))
        }

        exception<kotlinx.serialization.SerializationException> { call, cause ->
            // This handles JSON parsing errors (missing fields, wrong types)
            call.respond(HttpStatusCode.BadRequest, mapOf(
                "error" to "Invalid Input",
                "details" to (cause.message ?: "Malformed JSON or missing fields")
            ))
        }
    }

    install(RateLimit) {
        register {
            rateLimiter(limit = 60, refillPeriod = 60.seconds)
        }
    }

    /*
    install(CallLogging) {
        level = org.slf4j.event.Level.INFO
        filter { call -> call.request.path().startsWith("/api") }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            "Status: $status, HTTP method: $httpMethod, User agent: $userAgent"
        }
    }
    */
}
