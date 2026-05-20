package com.delivery.routes

import com.delivery.setupTestEnvironment
import com.delivery.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class AuthRoutesTest {

    @Test
    fun `register should create user and return 201 Created`() = testApplication {
        val client = setupTestEnvironment()

        val response = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest("Ivan", "ivan@example.com", "pass123", "1234567"))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val user = response.body<User>()
        assertEquals("ivan@example.com", user.email)
    }

    @Test
    fun `register with duplicate email should return 409 Conflict`() = testApplication {
        val client = setupTestEnvironment()
        val request = RegisterRequest("Ivan", "duplicate@example.com", "pass", "123")

        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        val secondResponse = client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        assertEquals(HttpStatusCode.Conflict, secondResponse.status)
    }

    @Test
    fun `login should return jwt token`() = testApplication {
        val client = setupTestEnvironment()

        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest("Ivan", "login@example.com", "pass123", "12345"))
        }

        val response = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("login@example.com", "pass123"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val loginData = response.body<LoginResponse>()
        assertTrue(loginData.token.isNotEmpty())
    }
}