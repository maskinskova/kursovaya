package com.delivery.plugins

import com.delivery.routes.*
import com.delivery.services.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val jwtConfig = environment.config.config("ktor.jwt")
    val secret = jwtConfig.property("secret").getString()
    val issuer = jwtConfig.property("issuer").getString()

    val menuService = MenuService()
    val orderService = OrderService()
    val authService = AuthService(secret, issuer)

    val testMode = environment.config.propertyOrNull("ktor.testMode")?.getString() == "true"

    routing {
        authRoutes(authService)

        if (testMode) {
            // В тестах – без проверки токенов
            menuRoutes(menuService)
            orderRoutes(orderService)
        } else {
            authenticate("jwt") {
                menuRoutes(menuService)
                orderRoutes(orderService)
            }
        }
    }
}