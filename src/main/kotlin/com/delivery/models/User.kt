package com.delivery.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Users : Table("users") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val phone = varchar("phone", 20)
    val role = varchar("role", 20).default("USER")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class User(
    val id: Int = 0,
    val name: String,
    val email: String,
    val phone: String,
    val role: String = "USER"
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
)