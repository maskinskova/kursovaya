package com.delivery.services

import at.favre.lib.crypto.bcrypt.BCrypt
import com.delivery.models.*
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.util.*

class AuthService(
    private val jwtSecret: String,
    private val jwtIssuer: String
) {
    private val jwtAlgorithm = Algorithm.HMAC256(jwtSecret)

    suspend fun register(request: RegisterRequest): User? = newSuspendedTransaction {
        val existing = Users.selectAll().where { Users.email eq request.email }.count()
        if (existing > 0) return@newSuspendedTransaction null

        val hashedPassword = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

        val newId = Users.insert {
            it[name] = request.name
            it[email] = request.email
            it[passwordHash] = hashedPassword
            it[phone] = request.phone
            it[role] = "USER"
        } get Users.id

        User(id = newId, name = request.name, email = request.email, phone = request.phone, role = "USER")
    }

    suspend fun login(request: LoginRequest): LoginResponse? = newSuspendedTransaction {
        val row = Users.selectAll().where { Users.email eq request.email }.singleOrNull() ?: return@newSuspendedTransaction null

        val storedHash = row[Users.passwordHash]
        val verified = BCrypt.verifyer().verify(request.password.toCharArray(), storedHash)
        if (!verified.verified) return@newSuspendedTransaction null

        val user = User(
            id = row[Users.id],
            name = row[Users.name],
            email = row[Users.email],
            phone = row[Users.phone],
            role = row[Users.role]
        )

        val token = JWT.create()
            .withSubject(user.id.toString())
            .withClaim("role", user.role)
            .withIssuer(jwtIssuer)
            .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000))
            .sign(jwtAlgorithm)

        LoginResponse(token = token, user = user)
    }

    fun verifyToken(token: String): JwtPrincipal? {
        return try {
            val verifier = JWT.require(jwtAlgorithm).withIssuer(jwtIssuer).build()
            val decoded = verifier.verify(token)
            JwtPrincipal(
                userId = decoded.subject.toInt(),
                role = decoded.getClaim("role").asString()
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class JwtPrincipal(val userId: Int, val role: String)