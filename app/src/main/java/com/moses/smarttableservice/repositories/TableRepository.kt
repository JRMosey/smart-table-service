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
                    val table = document.toObject(RestaurantTable::class.java)
                    table?.tableId = document.id
                    table
                }

                onSuccess(tables)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addTable(
        table: RestaurantTable,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val tableId = "table_${table.tableNumber}"
        table.tableId = tableId

        db.collection(FirebaseCollections.TABLES)
            .document(tableId)
            .set(table)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun updateTable(
        table: RestaurantTable,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        if (table.tableId.isBlank()) {
            table.tableId = "table_${table.tableNumber}"
        }

        db.collection(FirebaseCollections.TABLES)
            .document(table.tableId)
            .set(table)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun deleteTable(
        tableId: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.TABLES)
            .document(tableId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
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