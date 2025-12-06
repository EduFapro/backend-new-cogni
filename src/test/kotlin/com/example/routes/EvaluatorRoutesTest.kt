package com.example.routes

import com.example.models.Evaluator
import com.example.services.EvaluatorService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.Json
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import kotlin.test.*

class EvaluatorRoutesTest {

    private val service = mockk<EvaluatorService>()
    private val testSecret = "test-secret"
    private val testToken = JWT.create()
        .withAudience("http://0.0.0.0:8080/hello")
        .withIssuer("http://0.0.0.0:8080/")
        .sign(Algorithm.HMAC256(testSecret))

    @Test
    fun `test create evaluator success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            install(Authentication) {
                jwt("auth-jwt") {
                    verifier(JWT.require(Algorithm.HMAC256(testSecret)).build())
                    validate { JWTPrincipal(it.payload) }
                }
            }
            routing {
                evaluatorRoutes(service)
            }
        }

        val evaluator = Evaluator(
            id = 1,
            name = "John",
            surname = "Doe",
            email = "john.doe@example.com",
            birthDate = "1990-01-01",
            specialty = "Psychology",
            cpfOrNif = "12345678900",
            username = "johndoe",
            password = "password",
            firstLogin = true,
            isAdmin = false
        )

        every { service.create(any()) } returns 1

        val response = client.post("/api/evaluators") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Evaluator.serializer(), evaluator))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Evaluator created successfully"))
        verify { service.create(any()) }
    }

    @Test
    fun `test get evaluator success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            install(Authentication) {
                jwt("auth-jwt") {
                    verifier(JWT.require(Algorithm.HMAC256(testSecret)).build())
                    validate { JWTPrincipal(it.payload) }
                }
            }
            routing {
                evaluatorRoutes(service)
            }
        }

        val evaluator = Evaluator(
            id = 1,
            name = "John",
            surname = "Doe",
            email = "john.doe@example.com",
            birthDate = "1990-01-01",
            specialty = "Psychology",
            cpfOrNif = "12345678900",
            username = "johndoe",
            password = "password",
            firstLogin = true,
            isAdmin = false
        )

        every { service.getById(1) } returns evaluator

        val response = client.get("/api/evaluators/1") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("johndoe"))
        verify { service.getById(1) }
    }

    @Test
    fun `test get evaluator not found`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            install(Authentication) {
                jwt("auth-jwt") {
                    verifier(JWT.require(Algorithm.HMAC256(testSecret)).build())
                    validate { JWTPrincipal(it.payload) }
                }
            }
            routing {
                evaluatorRoutes(service)
            }
        }

        every { service.getById(999) } returns null

        val response = client.get("/api/evaluators/999") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        assertEquals(HttpStatusCode.NotFound, response.status)
        verify { service.getById(999) }
    }

    @Test
    fun `test update evaluator success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            install(Authentication) {
                jwt("auth-jwt") {
                    verifier(JWT.require(Algorithm.HMAC256(testSecret)).build())
                    validate { JWTPrincipal(it.payload) }
                }
            }
            routing {
                evaluatorRoutes(service)
            }
        }

        val evaluator = Evaluator(
            id = 1,
            name = "John",
            surname = "Doe",
            email = "john.doe@example.com",
            birthDate = "1990-01-01",
            specialty = "Psychology",
            cpfOrNif = "12345678900",
            username = "johndoe",
            password = "password",
            firstLogin = true,
            isAdmin = false
        )

        every { service.update(1, any()) } returns 1

        val response = client.put("/api/evaluators/1") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Evaluator.serializer(), evaluator))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Evaluator updated successfully"))
        verify { service.update(1, any()) }
    }

    @Test
    fun `test delete evaluator success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            install(Authentication) {
                jwt("auth-jwt") {
                    verifier(JWT.require(Algorithm.HMAC256(testSecret)).build())
                    validate { JWTPrincipal(it.payload) }
                }
            }
            routing {
                evaluatorRoutes(service)
            }
        }

        every { service.delete(1) } returns 1

        val response = client.delete("/api/evaluators/1") {
            header(HttpHeaders.Authorization, "Bearer $testToken")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Evaluator deleted successfully"))
        verify { service.delete(1) }
    }
}
