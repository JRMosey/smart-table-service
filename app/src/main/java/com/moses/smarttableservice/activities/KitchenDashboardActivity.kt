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
import com.moses.smarttableservice.models.Order
import com.moses.smarttableservice.repositories.OrderRepository

class KitchenDashboardActivity : AppCompatActivity() {

    private val orderRepository = OrderRepository()

    private lateinit var ordersContainer: LinearLayout

    private var ordersListener: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kitchen_dashboard)

        ordersContainer = findViewById(R.id.ordersContainer)

        startListeningToOrders()
    }

    override fun onDestroy() {
        super.onDestroy()
        ordersListener?.remove()
    }

    private fun startListeningToOrders() {
        ordersListener = orderRepository.listenToActiveKitchenOrders(
            onSuccess = { activeOrders ->
                displayOrders(activeOrders)
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun displayOrders(orders: List<Order>) {
        ordersContainer.removeAllViews()

        if (orders.isEmpty()) {
            val emptyText = TextView(this)
            emptyText.text = "No active kitchen orders"
            emptyText.textSize = 18f
            emptyText.setTextColor(Color.GRAY)
            ordersContainer.addView(emptyText)
            return
        }

        orders.forEach { order ->
            ordersContainer.addView(createOrderView(order))
        }
    }

    private fun createOrderView(order: Order): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 16)
        layout.setBackgroundColor(Color.WHITE)

        val title = TextView(this)
        title.text = "Order ${order.orderId.take(6)} - Table ${order.tableId}"
        title.textSize = 18f
        title.setTextColor(Color.BLACK)
        title.setPadding(0, 0, 0, 8)

        val itemsText = TextView(this)
        itemsText.text = order.items.joinToString("\n") {
            "- ${it.name} x${it.quantity}"
        }
        itemsText.textSize = 15f
        itemsText.setTextColor(Color.DKGRAY)
        itemsText.setPadding(0, 0, 0, 8)

        val statusText = TextView(this)
        statusText.text = "Status: ${order.status}"
        statusText.textSize = 15f
        statusText.setTextColor(Color.BLUE)
        statusText.setPadding(0, 0, 0, 12)

        val btnPreparing = Button(this)
        btnPreparing.text = "Mark as Preparing"

        val btnReady = Button(this)
        btnReady.text = "Mark as Ready"

        val btnServed = Button(this)
        btnServed.text = "Mark as Served"

        btnPreparing.setOnClickListener {
            updateOrderStatus(order.orderId, "preparing")
        }

        btnReady.setOnClickListener {
            updateOrderStatus(order.orderId, "ready")
        }

        btnServed.setOnClickListener {
            updateOrderStatus(order.orderId, "served")
        }

        layout.addView(title)
        layout.addView(itemsText)
        layout.addView(statusText)
        layout.addView(btnPreparing)
        layout.addView(btnReady)
        layout.addView(btnServed)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 16)
        layout.layoutParams = params

        return layout
    }

    private fun updateOrderStatus(orderId: String, status: String) {
        orderRepository.updateOrderStatus(
            orderId = orderId,
            status = status,
            onSuccess = {
                Toast.makeText(this, "Order updated to $status", Toast.LENGTH_SHORT).show()
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }
}