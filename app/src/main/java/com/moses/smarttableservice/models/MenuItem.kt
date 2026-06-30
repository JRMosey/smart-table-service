package com.moses.smarttableservice.models

import com.google.firebase.firestore.PropertyName

data class MenuItem(
    var itemId: String = "",
    var name: String = "",
    var description: String = "",
    var price: Double = 0.0,
    var category: String = "",
    var imageUrl: String = "",

    @get:PropertyName("isAvailable")
    @set:PropertyName("isAvailable")
    var isAvailable: Boolean = true,

    var addOns: List<AddOn> = emptyList()
)