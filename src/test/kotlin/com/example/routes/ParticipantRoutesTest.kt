package com.example.routes

import com.example.models.Participant
import com.example.services.ParticipantService
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

class ParticipantRoutesTest {

    private val service = mockk<ParticipantService>()

    @Test
    fun `test create participant success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                participantRoutes(service)
            }
        }

        val participant = Participant(
            id = 1,
            name = "Jane",
            surname = "Doe",
            birthDate = "2010-01-01",
            sex = 2,
            educationLevel = 1,
            laterality = 1,
            evaluatorId = 1
        )

        every { service.create(any()) } returns 1

        val response = client.post("/api/participants") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Participant.serializer(), participant))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Participant created successfully"))
        verify { service.create(any()) }
    }

    @Test
    fun `test get participant success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                participantRoutes(service)
            }
        }

        val participant = Participant(
            id = 1,
            name = "Jane",
            surname = "Doe",
            birthDate = "2010-01-01",
            sex = 2,
            educationLevel = 1,
            laterality = 1,
            evaluatorId = 1
        )

        every { service.getById(1) } returns participant

        val response = client.get("/api/participants/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Jane"))
        verify { service.getById(1) }
    }

    @Test
    fun `test get participants by evaluator id`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                participantRoutes(service)
            }
        }

        val participant = Participant(
            id = 1,
            name = "Jane",
            surname = "Doe",
            birthDate = "2010-01-01",
            sex = 2,
            educationLevel = 1,
            laterality = 1,
            evaluatorId = 1
        )

        every { service.getByEvaluatorId(1) } returns listOf(participant)

        val response = client.get("/api/participants?evaluatorId=1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Jane"))
        verify { service.getByEvaluatorId(1) }
    }

    @Test
    fun `test update participant success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                participantRoutes(service)
            }
        }

        val participant = Participant(
            id = 1,
            name = "Jane",
            surname = "Doe",
            birthDate = "2010-01-01",
            sex = 2,
            educationLevel = 1,
            laterality = 1,
            evaluatorId = 1
        )

        every { service.update(1, any()) } returns 1

        val response = client.put("/api/participants/1") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(Participant.serializer(), participant))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Participant updated successfully"))
        verify { service.update(1, any()) }
    }

    @Test
    fun `test delete participant success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                participantRoutes(service)
            }
        }

        every { service.delete(1) } returns 1

        val response = client.delete("/api/participants/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Participant deleted successfully"))
        verify { service.delete(1) }
    }
}
