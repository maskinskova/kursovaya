package com.delivery.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

object MenuItems : Table("menu_items") {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    val description = varchar("description", 500)
    val price = double("price")
    val category = varchar("category", 100)
    val isAvailable = bool("is_available").default(true)
    override val primaryKey = PrimaryKey(id)
}

@Serializable
data class MenuItem(
    val id: Int = 0,
    val name: String,
    val description: String,
    val price: Double,
    val category: String,
    val isAvailable: Boolean = true
)