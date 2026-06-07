package com.moses.smarttableservice.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.moses.smarttableservice.R
import com.moses.smarttableservice.databinding.ActivityRegisterBinding
import com.moses.smarttableservice.repositories.AuthRepository
import com.moses.smarttableservice.utils.Constants

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authRepository = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRoleDropdown()

        binding.tvBack.setOnClickListener { finish() }
        binding.tvGoToLogin.setOnClickListener { finish() }
        binding.btnCreateAccount.setOnClickListener { attemptRegister() }
    }

    private fun setupRoleDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, Constants.ROLE_OPTIONS)
        binding.actvRole.setAdapter(adapter)
    }

    private fun attemptRegister() {
        val name     = binding.etName.text.toString().trim()
        val email    = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirm  = binding.etConfirmPassword.text.toString().trim()
        val roleDisplay = binding.actvRole.text.toString().trim()

        // Clear errors
        binding.tilName.error = null
        binding.tilEmail.error = null
        binding.tilPassword.error = null
        binding.tilConfirmPassword.error = null
        binding.tilRole.error = null

        // Validate
        if (name.isEmpty()) { binding.tilName.error = getString(R.string.error_empty_fields); return }
        if (email.isEmpty()) { binding.tilEmail.error = getString(R.string.error_invalid_email); return }
        if (password.length < 6) { binding.tilPassword.error = getString(R.string.error_weak_password); return }
        if (password != confirm) { binding.tilConfirmPassword.error = getString(R.string.error_password_mismatch); return }
        if (roleDisplay.isEmpty()) { binding.tilRole.error = getString(R.string.error_select_role); return }

        val role = Constants.DISPLAY_TO_ROLE[roleDisplay] ?: run {
            binding.tilRole.error = getString(R.string.error_select_role)
            return
        }

        setLoading(true)

        authRepository.register(email, password, name, role) { success, error ->
            setLoading(false)
            if (success) {
                Toast.makeText(this, "$name added as ${Constants.ROLE_DISPLAY[role]}.", Toast.LENGTH_LONG).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                finish()
            } else {
                val message = when {
                    error?.contains("email address is already") == true -> getString(R.string.error_email_in_use)
                    else -> getString(R.string.error_register_failed)
                }
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
        binding.btnCreateAccount.isEnabled = !loading
        binding.etName.isEnabled = !loading
        binding.etEmail.isEnabled = !loading
        binding.etPassword.isEnabled = !loading
        binding.etConfirmPassword.isEnabled = !loading
        binding.actvRole.isEnabled = !loading
    }
}
