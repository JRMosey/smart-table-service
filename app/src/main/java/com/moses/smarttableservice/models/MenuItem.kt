package com.moses.smarttableservice.models

data class MenuItem(
    var itemId: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: String = "",
    var imageUrl: String = "",
    var isAvailable: Boolean = true
)