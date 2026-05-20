package com.delivery.routes

import com.delivery.setupTestEnvironment
import com.delivery.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import kotlin.test.*

class MenuRoutesTest {

    @Test
    fun `add menu item without ADMIN role should be forbidden`() = testApplication {
        val client = setupTestEnvironment()

        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest("User", "user@test.com", "pass", "123"))
        }
        val loginData = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("user@test.com", "pass"))
        }.body<LoginResponse>()

        val response = client.post("/menu") {
            bearerAuth(loginData.token)
            contentType(ContentType.Application.Json)
            setBody(MenuItem(name = "Pizza", description = "Tasty", price = 500.0, category = "Food"))
        }

        assertEquals(HttpStatusCode.Forbidden, response.status)
    }

    @Test
    fun `add menu item with ADMIN role should succeed`() = testApplication {
        val client = setupTestEnvironment()

        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest("Admin", "admin@test.com", "pass", "123"))
        }

        transaction {
            Users.update({ Users.email eq "admin@test.com" }) { it[role] = "ADMIN" }
        }

        val loginData = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("admin@test.com", "pass"))
        }.body<LoginResponse>()

        val response = client.post("/menu") {
            bearerAuth(loginData.token)
            contentType(ContentType.Application.Json)
            setBody(MenuItem(name = "Pizza", description = "Tasty", price = 500.0, category = "Food"))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val createdItem = response.body<MenuItem>()
        assertEquals("Pizza", createdItem.name)
    }
}