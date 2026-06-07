package com.moses.smarttableservice.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.moses.smarttableservice.models.User
import com.moses.smarttableservice.utils.FirebaseCollections

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCol = db.collection(FirebaseCollections.USERS)

    fun getAllUsers(onResult: (List<User>?, String?) -> Unit) {
        usersCol.get()
            .addOnSuccessListener { snapshot ->
                val users = snapshot.documents.mapNotNull { it.toObject(User::class.java) }
                onResult(users, null)
            }
            .addOnFailureListener { onResult(null, it.message) }
    }

    fun getUserById(uid: String, onResult: (User?, String?) -> Unit) {
        usersCol.document(uid).get()
            .addOnSuccessListener { onResult(it.toObject(User::class.java), null) }
            .addOnFailureListener { onResult(null, it.message) }
    }

    fun deleteUser(uid: String, onResult: (Boolean, String?) -> Unit) {
        usersCol.document(uid).delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }
}
