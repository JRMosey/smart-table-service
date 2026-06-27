package com.moses.smarttableservice.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.moses.smarttableservice.models.User
import com.moses.smarttableservice.utils.FirebaseCollections
import com.google.firebase.firestore.ListenerRegistration
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
    fun getUsers(

        onSuccess: (List<User>) -> Unit,

        onFailure: (Exception) -> Unit

    ) {

        db.collection(FirebaseCollections.USERS)

            .get()

            .addOnSuccessListener { result ->

                val users = result.documents.mapNotNull { document ->

                    val user = document.toObject(User::class.java)

                    user?.userId = document.id

                    user

                }



                onSuccess(users)

            }

            .addOnFailureListener { exception ->

                onFailure(exception)

            }

    }

    fun updateUserActiveStatus(
        userId: String,
        isActive: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.USERS)
            .document(userId)
            .update("isActive", isActive)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception -> onFailure(exception) }
    }
    fun listenToUsers(

        onSuccess: (List<User>) -> Unit,

        onFailure: (Exception) -> Unit

    ): ListenerRegistration {

        return db.collection(FirebaseCollections.USERS)

            .addSnapshotListener { snapshot, exception ->



                if (exception != null) {

                    onFailure(exception)

                    return@addSnapshotListener

                }



                val users = snapshot?.documents?.mapNotNull { document ->

                    val user = document.toObject(User::class.java)

                    user?.userId = document.id

                    user

                } ?: emptyList()



                onSuccess(users)

            }

    }
}