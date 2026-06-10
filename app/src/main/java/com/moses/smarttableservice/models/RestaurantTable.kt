package com.moses.smarttableservice.models

data class RestaurantTable(
    var tableId: String = "",
    var tableNumber: Int = 0,
    var name: String = "",
    var capacity: Int = 0,
    var status: String = "available",
    var currentOrderId: String? = null,
    var assignedWaiterId: String? = null
)