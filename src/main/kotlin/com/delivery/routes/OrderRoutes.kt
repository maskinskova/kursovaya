package com.delivery.routes

import com.delivery.models.Order
import com.delivery.models.OrderStatus
import com.delivery.services.OrderService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.orderRoutes(orderService: OrderService) {

    route("/orders") {

        // POST /orders
        post {
            val principal = call.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim("id")?.asInt()
                ?: return@post call.respond(HttpStatusCode.Unauthorized)

            val order = call.receive<Order>()
            val created = orderService.createOrder(order.copy(userId = userId))
            call.respond(HttpStatusCode.Created, created)
        }

        // GET /orders/user/{userId}
        get("/user/{userId}") {
            val principal = call.principal<JWTPrincipal>()
            val tokenUserId = principal?.payload?.getClaim("id")?.asInt()
            val tokenRole = principal?.payload?.getClaim("role")?.asString()
            val requestedUserId = call.parameters["userId"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Неверный ID")

            if (tokenUserId != requestedUserId && tokenRole != "ADMIN") {
                return@get call.respond(HttpStatusCode.Forbidden, "Доступ запрещён")
            }

            val orders = orderService.getOrdersByUser(requestedUserId)
            call.respond(orders)
        }

        // PATCH /orders/{id}/status
        patch("/{id}/status") {
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.payload?.getClaim("role")?.asString()
            if (role != "COURIER" && role != "ADMIN") {
                return@patch call.respond(HttpStatusCode.Forbidden, "Требуется роль COURIER или ADMIN")
            }

            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "Неверный ID")

            val status = call.receive<Map<String, String>>()["status"]
                ?: return@patch call.respond(HttpStatusCode.BadRequest, "Статус не указан")

            val updated = orderService.updateStatus(id, OrderStatus.valueOf(status))
                ?: return@patch call.respond(HttpStatusCode.NotFound, "Заказ не найден")

            call.respond(updated)
        }

        webSocket("/{id}/track") {
            val orderId = call.parameters["id"]?.toIntOrNull()
                ?: return@webSocket close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Неверный ID"))

            send("Подключено к отслеживанию заказа #$orderId")

            val job = launch {
                orderService.orderUpdates.collect { order ->
                    if (order.id == orderId) {
                        val message = Json.encodeToString(order)
                        send(message)
                    }
                }
            }

            for (frame in incoming) { /* обработка */ }
            job.cancel()
        }
    }
}