package com.moses.smarttableservice.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.moses.smarttableservice.models.User
import com.moses.smarttableservice.utils.FirebaseCollections

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createUser(user: User, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(FirebaseCollections.USERS)
            .document(user.userId)
            .set(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }

    fun getUserRole(userId: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(FirebaseCollections.USERS)
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val role = document.getString("role") ?: ""
                onSuccess(role)
            }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
}