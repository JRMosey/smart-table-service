package com.moses.smarttableservice.activities

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.moses.smarttableservice.R
import com.moses.smarttableservice.repositories.OrderRepository
import com.moses.smarttableservice.repositories.PaymentRepository
import com.moses.smarttableservice.repositories.TableRepository

class ManagerDashboardActivity : AppCompatActivity() {

    private val orderRepository = OrderRepository()
    private val tableRepository = TableRepository()
    private val paymentRepository = PaymentRepository()

    private lateinit var tvTotalOrders: TextView
    private lateinit var tvOccupiedTables: TextView
    private lateinit var tvRevenue: TextView
    private lateinit var tvAverageOrderValue: TextView
    private lateinit var tvTaxes : TextView
    private lateinit var tvTips: TextView
    private lateinit var tvCashPayments: TextView
    private lateinit var tvCardPayments: TextView
    private lateinit var tvOnlinePayments: TextView
    private lateinit var tvTopSellingItems : TextView
    private lateinit var btnStaffManagement: Button
    private lateinit var btnManageMenu: Button
    private lateinit var btnManageTables: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager_dashboard)

        tvTotalOrders = findViewById(R.id.tvTotalOrders)
        tvOccupiedTables = findViewById(R.id.tvOccupiedTables)
        tvRevenue = findViewById(R.id.tvRevenue)
        tvAverageOrderValue = findViewById(R.id.tvAverageOrderValue)
        tvTaxes = findViewById(R.id.tvTaxes)
        tvTips = findViewById(R.id.tvTips)
        tvCashPayments = findViewById(R.id.tvCashPayments)
        tvCardPayments = findViewById(R.id.tvCardPayments)
        tvOnlinePayments = findViewById(R.id.tvOnlinePayments)
        tvTopSellingItems = findViewById(R.id.tvTopSellingItems)
        btnStaffManagement = findViewById(R.id.btnStaffManagement)
        btnManageMenu = findViewById(R.id.btnManageMenu)
        btnManageTables = findViewById(R.id.btnManageTables)

        btnStaffManagement.setOnClickListener {
            val intent = Intent(this, StaffManagementActivity::class.java)
            startActivity(intent)
        }

        btnManageMenu.setOnClickListener {
            val intent = Intent(this, ManageMenuActivity::class.java)
            startActivity(intent)
        }

        btnManageTables.setOnClickListener {
            val intent = Intent(this, ManageTablesActivity::class.java)
            startActivity(intent)
        }

        loadDashboardData()
    }

    override fun onResume() {
        super.onResume()
        loadDashboardData()
    }

    private fun loadDashboardData() {
        loadOrdersStats()
        loadTablesStats()
        loadPaymentStats()
    }

    private fun loadOrdersStats() {
        orderRepository.getOrders(
            onSuccess = { orders ->
                val validOrders = orders.filter { it.status != "cancelled" }
                tvTotalOrders.text = "Total Orders\n${validOrders.size}"

                val itemSales = mutableMapOf<String, Int>()
                validOrders.forEach { order ->
                    order.items.forEach { item ->
                        itemSales[item.name] = (itemSales[item.name] ?: 0) + item.quantity
                    }
                }

                val topItemsText = itemSales.entries
                    .sortedByDescending { it.value }
                    .take(3)
                    .joinToString("\n") { "${it.key}: ${it.value}" }

                tvTopSellingItems.text = if (topItemsText.isNotEmpty()) {
                    "Top Selling Items\n$topItemsText"
                } else {
                    "Top Selling Items\nNo data yet"
                }
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

    private fun loadPaymentStats() {
        paymentRepository.getPayments(
            onSuccess = { payments ->
                val paidPayments = payments.filter { it.status == "paid" }
                val revenue = paidPayments.sumOf { it.amountPaid }
                val averageOrderValue = if (paidPayments.isNotEmpty()) revenue / paidPayments.size else 0.0
                val taxes = paidPayments.sumOf { it.taxAmount }
                val tips = paidPayments.sumOf { it.tipAmount }

                val cashTotal = paidPayments.filter { it.paymentMethod == "cash" }.sumOf { it.amountPaid }
                val cardTotal = paidPayments.filter { it.paymentMethod == "card" }.sumOf { it.amountPaid }
                val onlineTotal = paidPayments.filter { it.paymentMethod == "online" }.sumOf { it.amountPaid }

                tvRevenue.text = "Revenue\n$${"%.2f".format(revenue)}"
                tvAverageOrderValue.text = "Average Order Value\n$${"%.2f".format(averageOrderValue)}"
                tvTaxes.text = "Taxes\n$${"%.2f".format(taxes)}"
                tvTips.text = "Tips\n$${"%.2f".format(tips)}"
                tvCashPayments.text = "Cash Payments\n$${"%.2f".format(cashTotal)}"
                tvCardPayments.text = "Card Payments\n$${"%.2f".format(cardTotal)}"
                tvOnlinePayments.text = "Online Payments\n$${"%.2f".format(onlineTotal)}"
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }
}