package com.delivery

import com.delivery.db.*
import com.delivery.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init(environment)
    configureSerialization()
    configureWebSockets()
    configureSecurity()
    configureRouting()
}