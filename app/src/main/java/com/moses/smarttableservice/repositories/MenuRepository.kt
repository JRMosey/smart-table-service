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
}