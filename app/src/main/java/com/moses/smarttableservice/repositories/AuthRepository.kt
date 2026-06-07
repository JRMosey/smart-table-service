package com.moses.smarttableservice.repositories

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.moses.smarttableservice.models.User
import com.moses.smarttableservice.utils.FirebaseCollections

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun register(
        email: String,
        password: String,
        name: String,
        role: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = User(
                    uid = result.user!!.uid,
                    name = name,
                    email = email,
                    role = role,
                    createdAt = System.currentTimeMillis()
                )
                db.collection(FirebaseCollections.USERS)
                    .document(user.uid)
                    .set(user)
                    .addOnSuccessListener { onResult(true, null) }
                    .addOnFailureListener { onResult(false, it.message) }
            }
            .addOnFailureListener { onResult(false, it.message) }
    }

    fun logout() = auth.signOut()

    fun currentUserId(): String? = auth.currentUser?.uid
}
