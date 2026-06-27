package com.moses.smarttableservice.models

import com.google.firebase.firestore.PropertyName

data class User(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var role: String = "",

    @get:PropertyName("isActive")
    @set:PropertyName("isActive")
    var isActive: Boolean = true,

    var createdAt: Long = System.currentTimeMillis()
)