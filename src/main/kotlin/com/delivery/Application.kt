package com.delivery

import com.delivery.db.DatabaseFactory
import com.delivery.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*

fun main(args: Array<String>) = EngineMain.main(args)

fun Application.module() {
    DatabaseFactory.init()
    configureSerialization()
    configureWebSockets()
    configureSecurity()
    configureRouting()
}