package com.moses.smarttableservice.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.moses.smarttableservice.R
import com.moses.smarttableservice.databinding.ActivityKitchenDashboardBinding
import com.moses.smarttableservice.repositories.AuthRepository

class KitchenDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKitchenDashboardBinding
    private val authRepository = AuthRepository()

    data class OrderEntry(
        val table: Int,
        val items: String,
        val time: String,
        val status: String   // "pending" | "preparing" | "ready"
    )

    private val allOrders = listOf(
        OrderEntry(2, "Margherita Pizza, Caesar Salad", "12:05", "pending"),
        OrderEntry(4, "Ribeye Steak, Bruschetta × 2", "12:12", "preparing"),
        OrderEntry(1, "Tiramisu, Caesar Salad", "12:18", "ready"),
        OrderEntry(3, "Margherita Pizza", "12:22", "pending")
    )

    private var currentFilter = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKitchenDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        setupFilters()
        applyFilter("all")
    }

    private fun setupFilters() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when {
                checkedIds.contains(R.id.chipPending)   -> "pending"
                checkedIds.contains(R.id.chipPreparing) -> "preparing"
                checkedIds.contains(R.id.chipReady)     -> "ready"
                else                                    -> "all"
            }
            applyFilter(currentFilter)
        }
    }

    private fun applyFilter(filter: String) {
        val filtered = if (filter == "all") allOrders
                       else allOrders.filter { it.status == filter }

        if (filtered.isEmpty()) {
            binding.rvOrders.visibility = View.GONE
            binding.layoutEmptyOrders.visibility = View.VISIBLE
        } else {
            binding.layoutEmptyOrders.visibility = View.GONE
            binding.rvOrders.visibility = View.VISIBLE
            binding.rvOrders.layoutManager = LinearLayoutManager(this)
            binding.rvOrders.adapter = OrderQueueAdapter(filtered.toMutableList())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> { confirmLogout(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun confirmLogout() {
        AlertDialog.Builder(this)
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out?")
            .setPositiveButton("Sign Out") { _, _ ->
                authRepository.logout()
                startActivity(
                    Intent(this, LoginActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private inner class OrderQueueAdapter(
        private val orders: MutableList<OrderEntry>
    ) : RecyclerView.Adapter<OrderQueueAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val tvTable: TextView  = view.findViewById(R.id.tvOrderTable)
            val tvStatus: TextView = view.findViewById(R.id.tvOrderStatus)
            val tvTime: TextView   = view.findViewById(R.id.tvOrderTime)
            val tvItems: TextView  = view.findViewById(R.id.tvOrderItems)
            val tvTotal: TextView  = view.findViewById(R.id.tvOrderTotal)
            val btnAction: com.google.android.material.button.MaterialButton =
                view.findViewById(R.id.btnOrderAction)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(layoutInflater.inflate(R.layout.item_kitchen_order, parent, false))

        override fun getItemCount() = orders.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val order = orders[position]
            holder.tvTable.text  = "Table ${order.table}"
            holder.tvItems.text  = order.items
            holder.tvTime.text   = order.time
            holder.tvTotal.visibility = View.GONE

            val (label, hex, btnText) = when (order.status) {
                "preparing" -> Triple("Preparing", "#2563EB", "Mark Ready")
                "ready"     -> Triple("Ready",     "#059669", "Delivered ✓")
                else        -> Triple("Pending",   "#D97706", "Start Cooking")
            }
            holder.tvStatus.text = label
            holder.tvStatus.setTextColor(Color.parseColor(hex))
            holder.btnAction.text = btnText
            holder.btnAction.setOnClickListener {
                val next = when (order.status) {
                    "pending"   -> "preparing"
                    "preparing" -> "ready"
                    else        -> null
                }
                if (next != null) {
                    orders[position] = order.copy(status = next)
                    notifyItemChanged(position)
                } else {
                    orders.removeAt(position)
                    notifyItemRemoved(position)
                    if (orders.isEmpty()) applyFilter(currentFilter)
                }
                Toast.makeText(
                    this@KitchenDashboardActivity,
                    "Table ${order.table} updated",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
