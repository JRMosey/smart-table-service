package com.moses.smarttableservice.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.moses.smarttableservice.models.MenuItem
import com.moses.smarttableservice.utils.FirebaseCollections

class MenuRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getAvailableMenuItems(
        onSuccess: (List<MenuItem>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.MENU_ITEMS)
            .whereEqualTo("isAvailable", true)
            .get()
            .addOnSuccessListener { result ->
                val menuItems = result.documents.mapNotNull { document ->
                    document.toObject(MenuItem::class.java)
                }
                onSuccess(menuItems)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getAllMenuItems(
        onSuccess: (List<MenuItem>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.MENU_ITEMS)
            .get()
            .addOnSuccessListener { result ->
                val menuItems = result.documents.mapNotNull { document ->
                    document.toObject(MenuItem::class.java)
                }
                onSuccess(menuItems)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addMenuItem(
        item: MenuItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val docRef = db.collection(FirebaseCollections.MENU_ITEMS).document()
        item.itemId = docRef.id
        docRef.set(item)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun updateMenuItem(
        item: MenuItem,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.MENU_ITEMS)
            .document(item.itemId)
            .set(item)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun deleteMenuItem(
        itemId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.MENU_ITEMS)
            .document(itemId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}