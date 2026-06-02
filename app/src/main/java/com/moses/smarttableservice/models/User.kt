package com.moses.smarttableservice.models

data class User(
    var userId: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var role: String = "",
    var isActive: Boolean = true,
    var createdAt: Long = System.currentTimeMillis()
)