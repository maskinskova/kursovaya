package com.delivery

import com.delivery.models.MenuItem
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MenuApiTest {

    @Test
    fun `GET menu returns 200 and list`() = testApplication {
        application {
            module()
        }
        client.get("/menu").apply {
            assertEquals(HttpStatusCode.OK, status)
            val body = bodyAsText()
            assertTrue(body.startsWith("["))
        }
    }

    @Test
    fun `POST menu adds item and returns 201`() = testApplication {
        application { module() }
        val json = """
            {
                "name": "Test Pizza",
                "description": "Test",
                "price": 123.0,
                "category": "pizza",
                "isAvailable": true
            }
        """.trimIndent()

        client.post("/menu") {
            contentType(ContentType.Application.Json)
            setBody(json)
        }.apply {
            assertEquals(HttpStatusCode.Created, status)
            val item = Json.decodeFromString<MenuItem>(bodyAsText())
            assertEquals("Test Pizza", item.name)
            assertEquals(123.0, item.price)
        }
    }

    @Test
    fun `GET single menu item after creation`() = testApplication {
        application { module() }
        // Создаём
        val postResponse = client.post("/menu") {
            contentType(ContentType.Application.Json)
            setBody("""
                {"name":"Sushi","description":"Rice","price":9.99,"category":"rolls","isAvailable":true}
            """)
        }
        val created = Json.decodeFromString<MenuItem>(postResponse.bodyAsText())

        // Получаем по ID
        client.get("/menu/${created.id}").apply {
            assertEquals(HttpStatusCode.OK, status)
            val item = Json.decodeFromString<MenuItem>(bodyAsText())
            assertEquals("Sushi", item.name)
        }
    }
}