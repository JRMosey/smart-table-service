package com.moses.smarttableservice.models

data class OrderItem(
    var itemId: String = "",
    var name: String = "",
    var quantity: Int = 0,
    var unitPrice: Double = 0.0,
    var notes: String = "",
    var kitchenStatus: String = "pending"
)