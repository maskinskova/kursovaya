package com.delivery.plugins

import com.delivery.routes.*
import com.delivery.services.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val environment = this.environment
    val jwtConfig = environment.config.config("ktor.jwt")
    val jwtSecret = jwtConfig.property("secret").getString()
    val jwtIssuer = jwtConfig.property("issuer").getString()

    val menuService = MenuService()
    val orderService = OrderService()
    val authService = AuthService(jwtSecret, jwtIssuer)

    routing {
        authRoutes(authService)

        authenticate("jwt") {
            menuRoutes(menuService)
            orderRoutes(orderService)
        }
    }
}