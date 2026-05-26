package com.moses.smarttableservice.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SplashActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currentUser = auth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            checkUserRole(currentUser.uid)
        }
    }

    private fun checkUserRole(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                when (document.getString("role")) {
                    "manager" -> startActivity(Intent(this, ManagerDashboardActivity::class.java))
                    "waiter" -> startActivity(Intent(this, WaiterDashboardActivity::class.java))
                    "prepCook" -> startActivity(Intent(this, KitchenDashboardActivity::class.java))
                    else -> startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            }
            .addOnFailureListener {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
    }
}