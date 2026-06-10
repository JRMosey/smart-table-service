package com.moses.smarttableservice.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.moses.smarttableservice.models.RestaurantTable
import com.moses.smarttableservice.utils.FirebaseCollections

class TableRepository {

    private val db = FirebaseFirestore.getInstance()

    fun getTables(
        onSuccess: (List<RestaurantTable>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.TABLES)
            .get()
            .addOnSuccessListener { result ->
                val tables = result.documents.mapNotNull { document ->
                    document.toObject(RestaurantTable::class.java)
                }
                onSuccess(tables)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateTableStatus(
        tableId: String,
        status: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.TABLES)
            .document(tableId)
            .update("status", status)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}