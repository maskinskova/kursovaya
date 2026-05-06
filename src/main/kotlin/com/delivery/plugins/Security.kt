package com.delivery.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*

fun Application.configureSecurity() {
    val jwtConfig = environment.config.config("ktor.jwt")
    val jwtSecret = jwtConfig.property("secret").getString()
    val jwtIssuer = jwtConfig.property("issuer").getString()
    val realm = jwtConfig.property("realm").getString()

    install(Authentication) {
        jwt("jwt") {
            this.realm = realm
            verifier(
                JWT.require(Algorithm.HMAC256(jwtSecret))
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                if (credential.payload.subject != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}