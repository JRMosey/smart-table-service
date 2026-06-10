package com.moses.smarttableservice.activities

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.moses.smarttableservice.R
import com.moses.smarttableservice.repositories.OrderRepository
import com.moses.smarttableservice.repositories.TableRepository

class ManagerDashboardActivity : AppCompatActivity() {

    private val orderRepository = OrderRepository()
    private val tableRepository = TableRepository()

    private lateinit var tvTotalOrders: TextView
    private lateinit var tvOccupiedTables: TextView
    private lateinit var tvRevenue: TextView
    private lateinit var tvAverageOrderValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_dashboard)

        tvTotalOrders = findViewById(R.id.tvTotalOrders)
        tvOccupiedTables = findViewById(R.id.tvOccupiedTables)
        tvRevenue = findViewById(R.id.tvRevenue)
        tvAverageOrderValue = findViewById(R.id.tvAverageOrderValue)

        loadDashboardData()
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun loadDashboardData() {
        loadOrdersStats()
        loadTablesStats()
    }

    private fun loadOrdersStats() {
        orderRepository.getOrders(
            onSuccess = { orders ->
                val validOrders = orders.filter { it.status != "cancelled" }
                val revenueOrders = orders.filter {
                    it.status == "ready" || it.status == "served" || it.status == "paid"
                }

                val totalOrders = validOrders.size
                val revenue = revenueOrders.sumOf { it.total }
                val averageOrderValue = if (totalOrders > 0) revenue / totalOrders else 0.0

                tvTotalOrders.text = "Total Orders\n$totalOrders"
                tvRevenue.text = "Revenue\n$${"%.2f".format(revenue)}"
                tvAverageOrderValue.text = "Average Order Value\n$${"%.2f".format(averageOrderValue)}"
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun loadTablesStats() {
        tableRepository.getTables(
            onSuccess = { tables ->
                val occupiedTables = tables.count { it.status == "occupied" }
                tvOccupiedTables.text = "Occupied Tables\n$occupiedTables"
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }
}