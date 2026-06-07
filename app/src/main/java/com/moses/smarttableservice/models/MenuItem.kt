package com.moses.smarttableservice.models

/**
 * Mirrors the Firestore "menuItems" schema.
 *
 * All fields have defaults so this can also be deserialized later with
 * snapshot.toObject(MenuItem::class.java). (When you start reading
 * isAvailable from Firestore, you may need
 *   @get:PropertyName("isAvailable")
 * on it so the boolean field name maps correctly.)
 */
data class MenuItem(
    val itemId: String = "",
    val name: String = "",
    val description: String? = null,
    val price: Double = 0.0,
    val category: String = "",
    val imageUrl: String? = null,
    val isAvailable: Boolean = true
)
