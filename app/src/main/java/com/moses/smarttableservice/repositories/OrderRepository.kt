package com.moses.smarttableservice.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.moses.smarttableservice.models.Order
import com.moses.smarttableservice.utils.FirebaseCollections
import com.google.firebase.firestore.ListenerRegistration
class OrderRepository
{

    private val db = FirebaseFirestore.getInstance()

    fun createOrder(
        order: Order,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        val orderRef = db.collection(FirebaseCollections.ORDERS).document()

        order.orderId = orderRef.id

        orderRef.set(order)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getOrders(
        onSuccess: (List<Order>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        db.collection(FirebaseCollections.ORDERS)
            .get()
            .addOnSuccessListener { result ->

                val orders = result.documents.mapNotNull {
                    it.toObject(Order::class.java)
                }

                onSuccess(orders)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun updateOrderStatus(
        orderId: String,
        status: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {

        db.collection(FirebaseCollections.ORDERS)
            .document(orderId)
            .update(
                mapOf(
                    "status" to status,
                    "updatedAt" to System.currentTimeMillis()
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    fun getOrderById(
        orderId: String,
        onSuccess: (Order?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.ORDERS)
            .document(orderId)
            .get()
            .addOnSuccessListener { document ->
                val order = document.toObject(Order::class.java)
                onSuccess(order)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun listenToActiveKitchenOrders(
        onSuccess: (List<Order>) -> Unit,
        onFailure: (Exception) -> Unit
    ): ListenerRegistration {
        return db.collection(FirebaseCollections.ORDERS)
            .addSnapshotListener { snapshot, exception ->

                if (exception != null) {
                    onFailure(exception)
                    return@addSnapshotListener
                }

                val orders = snapshot?.documents?.mapNotNull {
                    it.toObject(Order::class.java)
                } ?: emptyList()

                val activeOrders = orders.filter {
                    it.status == "pending" ||
                            it.status == "preparing" ||
                            it.status == "ready"
                }

                onSuccess(activeOrders)
            }
    }
}