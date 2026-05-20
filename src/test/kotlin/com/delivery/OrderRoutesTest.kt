package com.delivery.routes

import com.delivery.setupTestEnvironment
import com.delivery.models.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class OrderRoutesTest {

    @Test
    fun `create order and fetch user orders should succeed`() = testApplication {
        val client = setupTestEnvironment()

        client.post("/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest("Client", "client@test.com", "pass", "123"))
        }
        val authData = client.post("/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest("client@test.com", "pass"))
        }.body<LoginResponse>()

        val newOrder = Order(
            userId = authData.user.id,
            items = listOf(OrderItem(menuItemId = 1, quantity = 2, price = 500.0)),
            totalPrice = 1000.0,
            address = "Pushkin str., 10"
        )

        val createResponse = client.post("/orders") {
            bearerAuth(authData.token)
            contentType(ContentType.Application.Json)
            setBody(newOrder)
        }

        assertEquals(HttpStatusCode.Created, createResponse.status)
        val createdOrder = createResponse.body<Order>()

        val getResponse = client.get("/orders/user/${authData.user.id}") {
            bearerAuth(authData.token)
        }

        assertEquals(HttpStatusCode.OK, getResponse.status)
        val orders = getResponse.body<List<Order>>()

        assertEquals(1, orders.size)
        assertEquals(createdOrder.id, orders[0].id)
    }
}