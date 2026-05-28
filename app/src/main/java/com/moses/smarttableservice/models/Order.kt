package com.moses.smarttableservice.models

data class Order(
    var orderId: String = "",
    var tableId: String = "",
    var waiterId: String = "",
    var orderType: String = "dine_in",
    var status: String = "pending",
    var items: List<OrderItem> = emptyList(),
    var subtotal: Double = 0.0,
    var taxAmount: Double = 0.0,
    var discountAmount: Double = 0.0,
    var total: Double = 0.0,
    var notes: String = "",
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)