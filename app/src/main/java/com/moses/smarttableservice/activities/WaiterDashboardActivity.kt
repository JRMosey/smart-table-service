package com.moses.smarttableservice.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.moses.smarttableservice.R
import com.moses.smarttableservice.models.Order
import com.moses.smarttableservice.models.RestaurantTable
import com.moses.smarttableservice.repositories.OrderRepository
import com.moses.smarttableservice.repositories.TableRepository

class WaiterDashboardActivity : AppCompatActivity() {

    private val tableRepository = TableRepository()
    private val orderRepository = OrderRepository()

    private lateinit var tableGrid: GridLayout
    private lateinit var btnCreateOrder: Button
    private lateinit var ordersToPayContainer: LinearLayout

    private var selectedTable: RestaurantTable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiter_dashboard)

        tableGrid = findViewById(R.id.tableGrid)
        btnCreateOrder = findViewById(R.id.btnCreateOrder)
        ordersToPayContainer = findViewById(R.id.ordersToPayContainer)

        btnCreateOrder.setOnClickListener {

            if (selectedTable == null) {

                Toast.makeText(
                    this,
                    "Please select a table first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (selectedTable?.status != "available") {

                Toast.makeText(
                    this,
                    "Selected table is not available",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val intent = Intent(
                this,
                CreateOrderActivity::class.java
            )

            intent.putExtra(
                "tableId",
                selectedTable!!.tableId
            )

            intent.putExtra(
                "tableNumber",
                selectedTable!!.tableNumber
            )

            startActivity(intent)
        }
        loadTables()
        loadOrdersToPay()
    }

    override fun onResume() {
        super.onResume()
        loadTables()
        loadOrdersToPay()
    }

    private fun loadTables() {
        tableRepository.getTables(
            onSuccess = { tables ->
                tableGrid.removeAllViews()
                tables.forEach { table ->
                    tableGrid.addView(createTableView(table))
                }
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadOrdersToPay() {
        orderRepository.getOrders(
            onSuccess = { orders ->
                ordersToPayContainer.removeAllViews()

                val payableOrders = orders.filter {
                    it.status == "served"
                }

                if (payableOrders.isEmpty()) {
                    val emptyText = TextView(this)
                    emptyText.text = "No orders ready for payment"
                    emptyText.setTextColor(Color.GRAY)
                    emptyText.textSize = 16f
                    ordersToPayContainer.addView(emptyText)
                    return@getOrders
                }

                payableOrders.forEach { order ->
                    ordersToPayContainer.addView(createPayOrderView(order))
                }
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun createPayOrderView(order: Order): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(16, 16, 16, 16)
        layout.setBackgroundColor(Color.WHITE)

        val text = TextView(this)
        text.text = "Order ${order.orderId.take(6)}\nTable: ${order.tableId}\nTotal: $${"%.2f".format(order.total)}"
        text.textSize = 16f
        text.setTextColor(Color.BLACK)

        val btnPay = Button(this)
        btnPay.text = "Pay Order"

        btnPay.setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java)
            intent.putExtra("orderId", order.orderId)
            startActivity(intent)
        }

        layout.addView(text)
        layout.addView(btnPay)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 12)
        layout.layoutParams = params

        return layout
    }

    private fun createTableView(table: RestaurantTable): TextView {

        val tableView = TextView(this)

        tableView.text = "Table ${table.tableNumber}\n${table.status}"
        tableView.gravity = Gravity.CENTER
        tableView.textSize = 16f
        tableView.setTextColor(Color.BLACK)
        tableView.setPadding(12, 12, 12, 12)

        val params = GridLayout.LayoutParams()
        params.width = 0
        params.height = 160
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
        params.setMargins(8, 8, 8, 8)

        tableView.layoutParams = params

        tableView.setBackgroundColor(
            getStatusColor(table.status)
        )

        tableView.setOnClickListener {

            when (table.status) {

                "available" -> {

                    selectedTable = table

                    Toast.makeText(
                        this,
                        "Table ${table.tableNumber} selected",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                "occupied" -> {

                    Toast.makeText(
                        this,
                        "Table ${table.tableNumber} is currently occupied",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                "reserved" -> {

                    Toast.makeText(
                        this,
                        "Table ${table.tableNumber} is reserved",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                "cleaning" -> {

                    Toast.makeText(
                        this,
                        "Table ${table.tableNumber} is being cleaned",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {

                    Toast.makeText(
                        this,
                        "Table ${table.tableNumber} is not available",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        return tableView
    }

    private fun getStatusColor(status: String): Int {
        return when (status) {
            "available" -> Color.parseColor("#D1FAE5")
            "occupied" -> Color.parseColor("#FEE2E2")
            "reserved" -> Color.parseColor("#FEF3C7")
            "cleaning" -> Color.parseColor("#DBEAFE")
            else -> Color.LTGRAY
        }
    }
}