package com.example.routes

import com.example.models.RecordingFile
import com.example.services.RecordingFileService
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

class RecordingFileRoutesTest {

    private val service = mockk<RecordingFileService>()

    @Test
    fun `test create recording file success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                recordingFileRoutes(service)
            }
        }

        val recordingFile = RecordingFile(
            id = 1,
            taskInstanceId = 1,
            filePath = "/path/to/file"
        )

        every { service.create(any()) } returns 1

        val response = client.post("/api/recordings") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(RecordingFile.serializer(), recordingFile))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Recording file created successfully"))
        verify { service.create(any()) }
    }

    @Test
    fun `test get recording file success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                recordingFileRoutes(service)
            }
        }

        val recordingFile = RecordingFile(
            id = 1,
            taskInstanceId = 1,
            filePath = "/path/to/file"
        )

        every { service.getById(1) } returns recordingFile

        val response = client.get("/api/recordings/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("/path/to/file"))
        verify { service.getById(1) }
    }

    @Test
    fun `test get recording file by task instance id`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                recordingFileRoutes(service)
            }
        }

        val recordingFile = RecordingFile(
            id = 1,
            taskInstanceId = 1,
            filePath = "/path/to/file"
        )

        every { service.getByTaskInstanceId(1) } returns recordingFile

        val response = client.get("/api/recordings/metadata/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("/path/to/file"))
        verify { service.getByTaskInstanceId(1) }
    }

    @Test
    fun `test delete recording file success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                recordingFileRoutes(service)
            }
        }

        every { service.delete(1) } returns 1

        val response = client.delete("/api/recordings/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Recording file deleted successfully"))
        verify { service.delete(1) }
    }
}
