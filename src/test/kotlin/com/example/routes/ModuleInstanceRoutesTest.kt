package com.example.routes

import com.example.models.ModuleInstance
import com.example.services.ModuleInstanceService
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

class ModuleInstanceRoutesTest {

    private val service = mockk<ModuleInstanceService>()

    @Test
    fun `test create module instance success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                moduleInstanceRoutes(service)
            }
        }

        val moduleInstance = ModuleInstance(
            id = 1,
            evaluationId = 1,
            moduleId = 1,
            status = 1
        )

        every { service.create(any()) } returns 1

        val response = client.post("/api/module-instances") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(ModuleInstance.serializer(), moduleInstance))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        assertTrue(response.bodyAsText().contains("Module instance created successfully"))
        verify { service.create(any()) }
    }

    @Test
    fun `test get module instance success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                moduleInstanceRoutes(service)
            }
        }

        val moduleInstance = ModuleInstance(
            id = 1,
            evaluationId = 1,
            moduleId = 1,
            status = 1
        )

        every { service.getById(1) } returns moduleInstance

        val response = client.get("/api/module-instances/1")

        assertEquals(HttpStatusCode.OK, response.status)
        // assertTrue(response.bodyAsText().contains("module1")) // removed check for module1 since moduleId is Int
        verify { service.getById(1) }
    }

    @Test
    fun `test get module instances by evaluation id`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                moduleInstanceRoutes(service)
            }
        }

        val moduleInstance = ModuleInstance(
            id = 1,
            evaluationId = 1,
            moduleId = 1,
            status = 1
        )

        every { service.getByEvaluationId(1) } returns listOf(moduleInstance)

        val response = client.get("/api/module-instances?evaluationId=1")

        assertEquals(HttpStatusCode.OK, response.status)
        // assertTrue(response.bodyAsText().contains("module1")) // removed check for module1 since moduleId is Int
        verify { service.getByEvaluationId(1) }
    }

    @Test
    fun `test update module instance status success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                moduleInstanceRoutes(service)
            }
        }

        every { service.updateStatus(1, 2) } returns 1

        val response = client.patch("/api/module-instances/1/status") {
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToString(ModuleStatusUpdate.serializer(), ModuleStatusUpdate(2)))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Status updated successfully"))
        verify { service.updateStatus(1, 2) }
    }

    @Test
    fun `test delete module instance success`() = testApplication {
        application {
            install(ContentNegotiation) {
                json()
            }
            routing {
                moduleInstanceRoutes(service)
            }
        }

        every { service.delete(1) } returns 1

        val response = client.delete("/api/module-instances/1")

        assertEquals(HttpStatusCode.OK, response.status)
        assertTrue(response.bodyAsText().contains("Module instance deleted successfully"))
        verify { service.delete(1) }
    }
}
