package com.delivery.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.delivery.models.MenuItems
import com.delivery.models.Orders
import com.delivery.models.Users
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/delivery"
            driverClassName = "org.postgresql.Driver"
            username = "postgres"
            password = "123456"
            maximumPoolSize = 10
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(MenuItems, Orders, Users)
        }
    }
}