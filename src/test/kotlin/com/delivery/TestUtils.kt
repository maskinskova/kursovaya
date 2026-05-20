package com.delivery

import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.config.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import java.util.UUID

fun ApplicationTestBuilder.setupTestEnvironment(): HttpClient {
    environment {
        config = MapApplicationConfig(
            "ktor.database.url" to "jdbc:h2:mem:test_${UUID.randomUUID()};DB_CLOSE_DELAY=-1",
            "ktor.database.driver" to "org.h2.Driver",
            "ktor.database.user" to "root",
            "ktor.database.password" to "",
            "ktor.database.maxPoolSize" to "1",
            "ktor.jwt.secret" to "test-secret",
            "ktor.jwt.issuer" to "test-issuer",
            "ktor.jwt.realm" to "test-realm"
        )
    }
    application {
        module()
    }
    // Создаем клиент, который сам работает с JSON
    return createClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true; isLenient = true })
        }
    }
}