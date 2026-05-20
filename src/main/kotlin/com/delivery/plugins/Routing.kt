package com.delivery.plugins

import com.delivery.routes.*
import com.delivery.services.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val jwtConfig = environment.config.config("ktor.jwt")
    val secret = jwtConfig.propertyOrNull("secret")?.getString() ?: "my-secret-key-change-me-in-production"
    val issuer = jwtConfig.propertyOrNull("issuer")?.getString() ?: "ktor-delivery"

    val menuService = MenuService()
    val orderService = OrderService()
    val authService = AuthService(secret, issuer)

    routing {
        authRoutes(authService)

        menuRoutes(menuService)

        authenticate("jwt") {
            orderRoutes(orderService)
        }
    }
}