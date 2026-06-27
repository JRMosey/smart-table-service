package com.moses.smarttableservice.activities

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ListenerRegistration
import com.moses.smarttableservice.R
import com.moses.smarttableservice.models.User
import com.moses.smarttableservice.repositories.UserRepository

class StaffManagementActivity : AppCompatActivity() {

    private val userRepository = UserRepository()

    private lateinit var staffContainer: LinearLayout

    private var usersListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_management)

        staffContainer = findViewById(R.id.staffContainer)

        startListeningToStaff()
    }

    override fun onDestroy() {
        super.onDestroy()
        usersListener?.remove()
    }

    private fun startListeningToStaff() {
        usersListener = userRepository.listenToUsers(
            onSuccess = { users ->
                displayStaff(users)
            },
            onFailure = { exception ->
                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun displayStaff(users: List<User>) {
        staffContainer.removeAllViews()

        if (users.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No staff members found"
            emptyText.textSize = 16f
            emptyText.setTextColor(Color.GRAY)
            staffContainer.addView(emptyText)
            return
        }

        users.forEach { user ->
            staffContainer.addView(createStaffView(user))
        }
    }

    private fun createStaffView(user: User): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setBackgroundColor(Color.WHITE)
        layout.setPadding(16, 16, 16, 16)

        val staffInfo = TextView(this)
        staffInfo.textSize = 16f
        staffInfo.setTextColor(Color.BLACK)

        updateStaffInfoText(staffInfo, user)

        val actionButton = Button(this)
        actionButton.text = if (user.isActive) "Deactivate" else "Reactivate"

        actionButton.setOnClickListener {
            val newStatus = !user.isActive

            actionButton.isEnabled = false

            userRepository.updateUserActiveStatus(
                userId = user.userId,
                isActive = newStatus,
                onSuccess = {
                    user.isActive = newStatus

                    updateStaffInfoText(staffInfo, user)

                    actionButton.text =
                        if (user.isActive) {
                            "Deactivate"
                        } else {
                            "Reactivate"
                        }

                    actionButton.isEnabled = true

                    Toast.makeText(
                        this,
                        "User status updated",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                onFailure = { exception ->
                    actionButton.isEnabled = true

                    Toast.makeText(
                        this,
                        exception.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }

        layout.addView(staffInfo)
        layout.addView(actionButton)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 12)
        layout.layoutParams = params

        return layout
    }

    private fun updateStaffInfoText(
        staffInfo: TextView,
        user: User
    ) {
        val role = formatRole(user.role)
        val status = if (user.isActive) {
            "🟢 Active"
        } else {
            "🔴 Inactive"
        }

        staffInfo.text =
            "${user.name}\n" +
                    "${user.email}\n" +
                    "Role: $role\n" +
                    "Status: $status"
    }

    private fun formatRole(role: String): String {
        return when (role) {
            "manager" -> "Manager"
            "waiter" -> "Waiter"
            "prepCook" -> "Prep Cook"
            else -> role
        }
    }
}