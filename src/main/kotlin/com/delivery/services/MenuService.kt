package com.delivery.services

import com.delivery.models.MenuItem
import com.delivery.models.MenuItems
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class MenuService {

    suspend fun getAllItems(): List<MenuItem> = newSuspendedTransaction {
        MenuItems.selectAll()
            .where { MenuItems.isAvailable eq true }
            .map { it.toMenuItem() }
    }

    suspend fun getByCategory(category: String): List<MenuItem> = newSuspendedTransaction {
        MenuItems.selectAll()
            .where { (MenuItems.category eq category) and (MenuItems.isAvailable eq true) }
            .map { it.toMenuItem() }
    }

    suspend fun getById(id: Int): MenuItem? = newSuspendedTransaction {
        MenuItems.selectAll()
            .where { MenuItems.id eq id }
            .map { it.toMenuItem() }
            .singleOrNull()
    }

    suspend fun addItem(item: MenuItem): MenuItem = newSuspendedTransaction {
        val statement = MenuItems.insert {
            it[MenuItems.name] = item.name
            it[MenuItems.description] = item.description
            it[MenuItems.price] = item.price
            it[MenuItems.category] = item.category
            it[MenuItems.isAvailable] = item.isAvailable
        }
        val newId = statement[MenuItems.id]
        item.copy(id = newId)
    }

    private fun ResultRow.toMenuItem() = MenuItem(
        id = this[MenuItems.id],
        name = this[MenuItems.name],
        description = this[MenuItems.description],
        price = this[MenuItems.price],
        category = this[MenuItems.category],
        isAvailable = this[MenuItems.isAvailable]
    )
}