package com.moses.smarttableservice.repositories

import com.google.firebase.firestore.FirebaseFirestore
import com.moses.smarttableservice.models.Payment
import com.moses.smarttableservice.utils.FirebaseCollections

class PaymentRepository {

    private val db = FirebaseFirestore.getInstance()

    fun createPayment(
        payment: Payment,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val paymentRef = db.collection(FirebaseCollections.PAYMENTS).document()

        payment.paymentId = paymentRef.id

        paymentRef.set(payment)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getPayments(
        onSuccess: (List<Payment>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(FirebaseCollections.PAYMENTS)
            .get()
            .addOnSuccessListener { result ->
                val payments = result.documents.mapNotNull {
                    it.toObject(Payment::class.java)
                }

                onSuccess(payments)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}