package com.delivery.routes

import com.delivery.models.LoginRequest
import com.delivery.models.RegisterRequest
import com.delivery.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {

    route("/auth") {
        // POST /auth/register
        post("/register") {
            try {
                val request = call.receive<RegisterRequest>()
                val user = authService.register(request)
                call.respond(HttpStatusCode.Created, user)
            } catch (e: IllegalStateException) {
                call.respond(HttpStatusCode.Conflict, mapOf("error" to e.message))
            }
        }

        // POST /auth/login
        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = authService.login(request)
            if (response != null) {
                call.respond(HttpStatusCode.OK, response)
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Неверный email или пароль"))
            }
        }
    }
}