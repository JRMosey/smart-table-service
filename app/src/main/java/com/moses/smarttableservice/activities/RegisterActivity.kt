package com.moses.smarttableservice.activities

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.moses.smarttableservice.R
import com.moses.smarttableservice.models.User
import com.moses.smarttableservice.repositories.UserRepository
import com.moses.smarttableservice.services.RoleRouterService

class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val spinnerRole = findViewById<Spinner>(R.id.spinnerRole)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        val roles = arrayOf("manager", "waiter", "prepCook")

        spinnerRole.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            roles
        )

        btnRegister.setOnClickListener {
            val name = edtName.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val role = spinnerRole.selectedItem.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val firebaseUser = auth.currentUser

                    if (firebaseUser != null) {
                        val user = User(
                            userId = firebaseUser.uid,
                            name = name,
                            email = email,
                            role = role,
                            isActive = true
                        )

                        userRepository.createUser(
                            user,
                            onSuccess = {
                                Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                                RoleRouterService.routeUser(this, role)
                                finish()
                            },
                            onFailure = { exception ->
                                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                }
        }
    }
}