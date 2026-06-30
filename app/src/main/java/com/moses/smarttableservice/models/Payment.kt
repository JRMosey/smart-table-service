package com.moses.smarttableservice.models

data class Payment(
    var paymentId: String = "",
    var orderId: String = "",
    var tableId: String = "",
    var amountPaid: Double = 0.0,
    var subtotal: Double = 0.0,
    var taxAmount: Double = 0.0,
    var tipAmount: Double = 0.0,
    var paymentMethod: String = "cash",
    var status: String = "paid",
    var createdAt: Long = System.currentTimeMillis()
)