package com.moses.smarttableservice.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moses.smarttableservice.R
import com.moses.smarttableservice.databinding.ActivityWaiterDashboardBinding
import com.moses.smarttableservice.repositories.AuthRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WaiterDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWaiterDashboardBinding
    private val authRepository = AuthRepository()

    data class TableItem(
        val number: Int,
        val status: String,
        val price: Double = 0.0,
        val minutes: Int = 0
    )

    data class OrderItem(
        val tableNumber: Int,
        val status: String,
        val price: Double,
        val minutes: Int
    )

    private val allTables = listOf(
        TableItem(1, "occupied", 45.50, 15),
        TableItem(2, "available"),
        TableItem(3, "waiting", 62.00, 8),
        TableItem(4, "available"),
        TableItem(5, "ready", 85.25, 22),
        TableItem(6, "occupied", 32.00, 5),
        TableItem(7, "available"),
        TableItem(8, "waiting", 94.50, 12),
        TableItem(9, "available"),
        TableItem(10, "occupied", 58.75, 18),
        TableItem(11, "available"),
        TableItem(12, "ready", 124.00, 25)
    )

    private val activeOrders = listOf(
        OrderItem(1, "Occupied", 45.50, 15),
        OrderItem(3, "Waiting", 62.00, 8),
        OrderItem(5, "Ready", 85.25, 22),
        OrderItem(6, "Occupied", 32.00, 5),
        OrderItem(8, "Waiting", 94.50, 12),
        OrderItem(10, "Occupied", 58.75, 18),
        OrderItem(12, "Ready", 124.00, 25)
    )

    private var currentFilter = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWaiterDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupTime()
        setupTabs()
        setupTablesGrid(allTables)
        setupOrdersList()

        binding.ivLogout.setOnClickListener { confirmLogout() }
        binding.fabNewOrder.setOnClickListener {
            Toast.makeText(this, "Select a table to start a new order", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTime() {
        val fmt = SimpleDateFormat("hh:mm a", Locale.getDefault())
        binding.tvCurrentTime.text = "🕐 ${fmt.format(Date())}"
    }

    private fun setupTabs() {
        val tabs = listOf(binding.tabAll, binding.tabAvailable, binding.tabOccupied)

        fun selectTab(selected: TextView, filter: String) {
            tabs.forEach { tab ->
                if (tab == selected) {
                    tab.setBackgroundResource(R.drawable.bg_tab_selected)
                    tab.setTextColor(Color.WHITE)
                    tab.setTypeface(null, android.graphics.Typeface.BOLD)
                } else {
                    tab.background = null
                    tab.setTextColor(Color.parseColor("#64748B"))
                    tab.setTypeface(null, android.graphics.Typeface.NORMAL)
                }
            }
            currentFilter = filter
            val filtered = when (filter) {
                "available" -> allTables.filter { it.status == "available" }
                "occupied" -> allTables.filter { it.status != "available" }
                else -> allTables
            }
            setupTablesGrid(filtered)
        }

        binding.tabAll.setOnClickListener { selectTab(binding.tabAll, "all") }
        binding.tabAvailable.setOnClickListener { selectTab(binding.tabAvailable, "available") }
        binding.tabOccupied.setOnClickListener { selectTab(binding.tabOccupied, "occupied") }
    }

    private fun setupTablesGrid(tables: List<TableItem>) {
        binding.rvTables.layoutManager = GridLayoutManager(this, 3)
        binding.rvTables.adapter = TableGridAdapter(tables)
    }

    private fun setupOrdersList() {
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = OrdersAdapter(activeOrders)
        binding.layoutEmptyOrders.visibility = if (activeOrders.isEmpty()) View.VISIBLE else View.GONE
        binding.rvOrders.visibility = if (activeOrders.isEmpty()) View.GONE else View.VISIBLE
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

    private fun statusColor(status: String): Int = when (status.lowercase()) {
        "occupied" -> Color.parseColor("#3B82F6")
        "waiting"  -> Color.parseColor("#F59E0B")
        "ready"    -> Color.parseColor("#8B5CF6")
        else       -> Color.parseColor("#22C55E")
    }

    private fun statusLabel(status: String): String = when (status.lowercase()) {
        "occupied" -> "Occupied"
        "waiting"  -> "Waiting"
        "ready"    -> "Ready"
        else       -> "Available"
    }

    // --- Table grid adapter ---

    inner class TableGridAdapter(
        private val tables: List<TableItem>
    ) : RecyclerView.Adapter<TableGridAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val cardRoot = view.findViewById<com.google.android.material.card.MaterialCardView>(R.id.cardTable)
            val dot: View = view.findViewById(R.id.viewStatusDot)
            val tvNum: TextView = view.findViewById(R.id.tvTableNum)
            val tvStatus: TextView = view.findViewById(R.id.tvTableStatus)
            val tvMeta: TextView = view.findViewById(R.id.tvTableMeta)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(layoutInflater.inflate(R.layout.item_table_card, parent, false))

        override fun getItemCount() = tables.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = tables[position]
            val color = statusColor(item.status)

            // Colored dot
            val dotDrawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
            }
            holder.dot.background = dotDrawable

            holder.tvNum.text = item.number.toString()
            holder.tvNum.setTextColor(Color.parseColor("#1E293B"))
            holder.tvStatus.text = statusLabel(item.status)
            holder.tvStatus.setTextColor(color)

            if (item.status != "available") {
                holder.tvMeta.visibility = View.VISIBLE
                holder.tvMeta.text = "${"$"}${"%.2f".format(item.price)} · ${item.minutes}m"
            } else {
                holder.tvMeta.visibility = View.GONE
            }

            // Green border for available tables
            if (item.status == "available") {
                holder.cardRoot.strokeColor = Color.parseColor("#22C55E")
                holder.cardRoot.strokeWidth = 2
            } else {
                holder.cardRoot.strokeColor = Color.parseColor("#E2E8F0")
                holder.cardRoot.strokeWidth = 1
            }

            holder.itemView.setOnClickListener {
                Toast.makeText(
                    this@WaiterDashboardActivity,
                    "Table ${item.number} · ${statusLabel(item.status)}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    // --- Active Orders adapter ---

    inner class OrdersAdapter(
        private val orders: List<OrderItem>
    ) : RecyclerView.Adapter<OrdersAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val badgeBg: View = view.findViewById(R.id.viewBadgeBg)
            val tvBadgeNum: TextView = view.findViewById(R.id.tvBadgeNum)
            val tvTitle: TextView = view.findViewById(R.id.tvOrderTitle)
            val tvMeta: TextView = view.findViewById(R.id.tvOrderMeta)
            val tvPrice: TextView = view.findViewById(R.id.tvOrderPrice)
            val tvCheck: TextView = view.findViewById(R.id.tvReadyCheck)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(layoutInflater.inflate(R.layout.item_order_card, parent, false))

        override fun getItemCount() = orders.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = orders[position]
            val color = statusColor(item.status)

            val badgeDrawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = resources.displayMetrics.density * 10
                setColor(color)
            }
            holder.badgeBg.background = badgeDrawable
            holder.tvBadgeNum.text = item.tableNumber.toString()
            holder.tvTitle.text = "Table ${item.tableNumber}"
            holder.tvMeta.text = "${item.status} · ${item.minutes}m"
            holder.tvPrice.text = "${"$"}${"%.2f".format(item.price)}"
            holder.tvCheck.visibility = if (item.status.equals("ready", ignoreCase = true)) View.VISIBLE else View.GONE

            // Divider except last item
            holder.itemView.background = if (position < orders.size - 1) {
                android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT)
            } else null
        }
    }
}
