package com.example.routes

import com.example.models.Evaluation
import com.example.services.EvaluationService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import io.mockk.*
import kotlinx.serialization.json.Json
import kotlin.test.*

class EvaluationRoutesTest {

    private val service = mockk<EvaluationService>()

    @Test
    fun `test create evaluation success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                evaluationRoutes(service)
            }
        }

        val evaluation = Evaluation(
            id = 1,
            evaluatorId = 1,
            participantId = 1,
            evaluationDate = "2023-01-01",
            status = 1,
            language = 1
        )

        every { service.create(any()) } returns 1

        val response = client.post("/api/evaluations") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Evaluation.serializer(), evaluation))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Evaluation created successfully"))
        verify { service.create(any()) }
    }

    @Test
    fun `test get evaluation success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                evaluationRoutes(service)
            }
        }

        val evaluation = Evaluation(
            id = 1,
            evaluatorId = 1,
            participantId = 1,
            evaluationDate = "2023-01-01",
            status = 1,
            language = 1
        )

        every { service.getById(1) } returns evaluation

        val response = client.get("/api/evaluations/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("2023-01-01"))
        verify { service.getById(1) }
    }

    @Test
    fun `test update evaluation success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                evaluationRoutes(service)
            }
        }

        val evaluation = Evaluation(
            id = 1,
            evaluatorId = 1,
            participantId = 1,
            evaluationDate = "2023-01-01",
            status = 1,
            language = 1
        )

        every { service.update(1, any()) } returns 1

        val response = client.put("/api/evaluations/1") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Evaluation.serializer(), evaluation))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Evaluation updated successfully"))
        verify { service.update(1, any()) }
    }

    @Test
    fun `test update evaluation status success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                evaluationRoutes(service)
            }
        }

        every { service.updateStatus(1, 2) } returns 1

        val response = client.patch("/api/evaluations/1/status") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(StatusUpdate.serializer(), StatusUpdate(2)))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Status updated successfully"))
        verify { service.updateStatus(1, 2) }
    }
}
