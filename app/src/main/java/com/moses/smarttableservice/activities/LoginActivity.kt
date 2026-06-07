package com.moses.smarttableservice.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.moses.smarttableservice.databinding.ActivityLoginBinding
import com.moses.smarttableservice.repositories.AuthRepository

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignIn.setOnClickListener { attemptLogin() }

        binding.tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun attemptLogin() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // Clear previous errors
        binding.tilEmail.error = null
        binding.tilPassword.error = null

        if (email.isEmpty()) {
            binding.tilEmail.error = getString(com.moses.smarttableservice.R.string.error_invalid_email)
            return
        }
        if (password.isEmpty()) {
            binding.tilPassword.error = getString(com.moses.smarttableservice.R.string.error_weak_password)
            return
        }

        setLoading(true)

        authRepository.login(email, password) { success, error ->
            setLoading(false)
            if (success) {
                // SplashActivity handles role-based routing
                val intent = Intent(this, SplashActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                val message = when {
                    error?.contains("no user record") == true -> "No account found with this email."
                    error?.contains("password is invalid") == true -> "Incorrect password."
                    error?.contains("INVALID_LOGIN_CREDENTIALS") == true -> "Invalid email or password."
                    else -> getString(com.moses.smarttableservice.R.string.error_login_failed)
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnSignIn.isEnabled = !loading
        binding.etEmail.isEnabled = !loading
        binding.etPassword.isEnabled = !loading
    }
}
