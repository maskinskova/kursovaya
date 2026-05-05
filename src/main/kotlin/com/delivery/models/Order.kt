package com.delivery.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object Orders : Table("orders") {
    val id = integer("id").autoIncrement()
    val userId = integer("user_id")
    val items = text("items")
    val totalPrice = double("total_price")
    val status = varchar("status", 50).default("PENDING")
    val address = varchar("address", 500)
    val createdAt = long("created_at")
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class Order(
    val id: Int = 0,
    val userId: Int,
    val items: List<OrderItem>,
    val totalPrice: Double,
    val status: OrderStatus = OrderStatus.PENDING,
    val address: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class OrderItem(
    val menuItemId: Int,
    val quantity: Int,
    val price: Double
)

@Serializable
enum class OrderStatus {
    PENDING,
    CONFIRMED,
    PREPARING,
    ON_THE_WAY,
    DELIVERED,
    CANCELLED
}