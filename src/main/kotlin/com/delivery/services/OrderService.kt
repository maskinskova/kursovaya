package com.delivery.services

import com.delivery.models.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class OrderService {

    private val _orderUpdates = MutableSharedFlow<Order>(replay = 1)
    val orderUpdates: SharedFlow<Order> = _orderUpdates.asSharedFlow()

    suspend fun createOrder(order: Order): Order = newSuspendedTransaction {
        val newId = Orders.insert {
            it[userId] = order.userId
            it[items] = Json.encodeToString(order.items)
            it[totalPrice] = order.totalPrice
            it[status] = order.status.name
            it[address] = order.address
            it[createdAt] = order.createdAt
        } get Orders.id

        val newOrder = order.copy(id = newId)
        _orderUpdates.emit(newOrder)
        newOrder
    }

    suspend fun getOrdersByUser(userId: Int): List<Order> = newSuspendedTransaction {
        Orders.selectAll()
            .where { Orders.userId eq userId }
            .map { it.toOrder() }
    }

    suspend fun updateStatus(orderId: Int, status: OrderStatus): Order? {
        val updated = newSuspendedTransaction {
            Orders.update({ Orders.id eq orderId }) {
                it[Orders.status] = status.name
            }
            Orders.selectAll()
                .where { Orders.id eq orderId }
                .map { it.toOrder() }
                .singleOrNull()
        }
        updated?.let { _orderUpdates.emit(it) }
        return updated
    }

    private fun ResultRow.toOrder() = Order(
        id = this[Orders.id],
        userId = this[Orders.userId],
        items = Json.decodeFromString(this[Orders.items]),
        totalPrice = this[Orders.totalPrice],
        status = OrderStatus.valueOf(this[Orders.status]),
        address = this[Orders.address],
        createdAt = this[Orders.createdAt]
    )
}