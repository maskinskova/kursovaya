package com.delivery.routes

import com.delivery.services.MenuService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.menuRoutes(menuService: MenuService) {

    route("/menu") {

        get {
            val category = call.request.queryParameters["category"]
            val items = if (category != null) {
                menuService.getByCategory(category)
            } else {
                menuService.getAllItems()
            }
            call.respond(items)
        }

        get("/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Неверный ID")

            val item = menuService.getById(id)
                ?: return@get call.respond(HttpStatusCode.NotFound, "Блюдо не найдено")

            call.respond(item)
        }

        post {
            // Только ADMIN
            val principal = call.principal<JWTPrincipal>()
            val role = principal?.payload?.getClaim("role")?.asString()
            if (role != "ADMIN") {
                call.respond(HttpStatusCode.Forbidden, "Требуется роль ADMIN")
                return@post
            }

            val item = call.receive<com.delivery.models.MenuItem>()
            val created = menuService.addItem(item)
            call.respond(HttpStatusCode.Created, created)
        }
    }
}