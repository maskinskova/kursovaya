package com.delivery.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.delivery.models.MenuItems
import com.delivery.models.Orders
import com.delivery.models.Users
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    fun init(env: ApplicationEnvironment) {
        val dbConfig = env.config.config("ktor.database")
        val url = dbConfig.property("url").getString()
        val driver = dbConfig.property("driver").getString()
        val user = dbConfig.property("user").getString()
        val password = dbConfig.property("password").getString()
        val maxPoolSize = dbConfig.property("maxPoolSize").getString().toInt()

        val config = HikariConfig().apply {
            jdbcUrl = url
            driverClassName = driver
            username = user
            this.password = password
            maximumPoolSize = maxPoolSize
        }
        val dataSource = HikariDataSource(config)
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(MenuItems, Orders, Users)
        }
    }
}