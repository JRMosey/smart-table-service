package com.moses.smarttableservice.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.moses.smarttableservice.adapters.StaffAdapter
import com.moses.smarttableservice.databinding.ActivityStaffListBinding
import com.moses.smarttableservice.models.User
import com.moses.smarttableservice.repositories.UserRepository
import com.moses.smarttableservice.utils.Constants

class StaffListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStaffListBinding
    private lateinit var adapter: StaffAdapter
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStaffListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()

        binding.fabAddStaff.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        loadStaff()
    }

    override fun onResume() {
        super.onResume()
        // Refresh list when returning from RegisterActivity
        loadStaff()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = StaffAdapter(emptyList()) { user -> showUserOptions(user) }
        binding.rvStaff.layoutManager = LinearLayoutManager(this)
        binding.rvStaff.adapter = adapter
    }

    private fun loadStaff() {
        binding.progressBar.visibility = View.VISIBLE
        binding.layoutEmpty.visibility = View.GONE

        userRepository.getAllUsers { users, error ->
            binding.progressBar.visibility = View.GONE

            if (error != null) {
                Toast.makeText(this, "Failed to load staff: $error", Toast.LENGTH_SHORT).show()
                return@getAllUsers
            }

            if (users.isNullOrEmpty()) {
                binding.layoutEmpty.visibility = View.VISIBLE
                adapter.updateList(emptyList())
            } else {
                binding.layoutEmpty.visibility = View.GONE
                // Sort: managers first, then waiters, then kitchen
                val sorted = users.sortedBy {
                    when (it.role) {
                        Constants.ROLE_MANAGER   -> 0
                        Constants.ROLE_WAITER    -> 1
                        else                     -> 2
                    }
                }
                adapter.updateList(sorted)
            }
        }
    }

    private fun showUserOptions(user: User) {
        val roleLabel = Constants.ROLE_DISPLAY[user.role] ?: user.role
        AlertDialog.Builder(this)
            .setTitle(user.name)
            .setMessage("Email: ${user.email}\nRole: $roleLabel")
            .setNegativeButton("Close", null)
            .setPositiveButton("Remove") { _, _ -> confirmDelete(user) }
            .show()
    }

    private fun confirmDelete(user: User) {
        AlertDialog.Builder(this)
            .setTitle("Remove ${user.name}?")
            .setMessage("This will delete their account data from Firestore. They will still be able to log in until their Auth account is also removed.")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Remove") { _, _ ->
                userRepository.deleteUser(user.uid) { success, error ->
                    if (success) {
                        Toast.makeText(this, "${user.name} removed.", Toast.LENGTH_SHORT).show()
                        loadStaff()
                    } else {
                        Toast.makeText(this, "Failed: $error", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .show()
    }
}
