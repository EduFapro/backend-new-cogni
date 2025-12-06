package com.example

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import com.example.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureDatabase()
        configureSecurity()
        configureMonitoring()
        configureOpenAPI()
        configureRouting()
    }.start(wait = true)
}
