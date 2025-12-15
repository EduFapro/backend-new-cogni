package com.example.routes

import com.example.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

@Serializable
data class ForgotPasswordRequest(
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    val token: String,
    val newPassword: String
)

fun Route.authRoutes() {
    val authService = AuthService()

    route("/api/auth") {
        // Protected: Change Password
        authenticate("auth-jwt") {
            post("/change-password") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("id")?.asString()

                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token"))
                    return@post
                }

                val request = try {
                    call.receive<ChangePasswordRequest>()
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid JSON"))
                    return@post
                }

                val success = authService.changePassword(userId, request.oldPassword, request.newPassword)
                if (success) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Password changed successfully"))
                } else {
                    call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid old password or user not found"))
                }
            }
        }

        // Public: Request Password Reset
        post("/request-password-reset") {
            val request = try {
                call.receive<ForgotPasswordRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid JSON"))
                return@post
            }

            val token = authService.requestPasswordReset(request.email)
            // Always respond OK to prevent email enumeration, unless dev mode
            if (token != null) {
                // In prod, don't send token back. In dev, we might for easier testing.
                // For this task, we'll just say email sent.
                call.respond(HttpStatusCode.OK, mapOf("message" to "If email exists, reset link sent."))
            } else {
                call.respond(HttpStatusCode.OK, mapOf("message" to "If email exists, reset link sent."))
            }
        }

        // Public: Reset Password
        post("/reset-password") {
            val request = try {
                call.receive<ResetPasswordRequest>()
            } catch (e: Exception) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid JSON"))
                return@post
            }

            val success = authService.resetPassword(request.token, request.newPassword)
            if (success) {
                call.respond(HttpStatusCode.OK, mapOf("message" to "Password reset successfully"))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid or expired token"))
            }
        }
    }
}
