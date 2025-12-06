package com.example.routes

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import com.example.plugins.*

import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm

class StatusRouteTest {
    @Test
    fun testStatusEndpoint() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            install(Authentication) {
                jwt("auth-jwt") {
                    verifier(JWT.require(Algorithm.HMAC256("test-secret")).build())
                    validate { JWTPrincipal(it.payload) }
                }
            }
            configureRouting()
        }
        client.get("/api/status").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertTrue(bodyAsText().contains("status"))
            assertTrue(bodyAsText().contains("mode"))
        }
    }
}
