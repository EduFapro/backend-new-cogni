package com.example

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import com.example.plugins.*

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    embeddedServer(Netty, port = port, host = "0.0.0.0") {
        configureSerialization()
        configureDatabase()
        configureSecurity()
        configureMonitoring()
        configureOpenAPI()
        configureRouting()
    }.start(wait = true)
}
