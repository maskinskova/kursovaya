package com.delivery.plugins

import com.delivery.routes.menuRoutes
import com.delivery.routes.orderRoutes
import com.delivery.routes.authRoutes
import com.delivery.services.MenuService
import com.delivery.services.OrderService
import com.delivery.services.AuthService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val menuService = MenuService()
    val orderService = OrderService()
    val authService = AuthService()

    routing {
        authRoutes(authService)

        authenticate("jwt") {
            menuRoutes(menuService)
        }

        authenticate("jwt") {
            orderRoutes(orderService)
        }
    }
}