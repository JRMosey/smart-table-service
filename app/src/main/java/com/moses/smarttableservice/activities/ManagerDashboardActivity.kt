package com.moses.smarttableservice.activities

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.moses.smarttableservice.R
import com.moses.smarttableservice.databinding.ActivityManagerDashboardBinding
import com.moses.smarttableservice.repositories.AuthRepository
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ManagerDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManagerDashboardBinding
    private val authRepository = AuthRepository()

    data class ActivityItem(
        val description: String,
        val timeAgo: String,
        val price: Double,
        val dotColor: String
    )

    private val recentActivity = listOf(
        ActivityItem("Table 7 - Order completed", "2 minutes ago", 85.50, "#22C55E"),
        ActivityItem("Table 3 - New order", "5 minutes ago", 62.00, "#F59E0B"),
        ActivityItem("Table 12 - Payment received", "8 minutes ago", 124.75, "#3B82F6")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManagerDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDate()
        setupRevenueChart()
        setupOrdersChart()
        setupRecentActivity()

        binding.ivLogoutManager.setOnClickListener { confirmLogout() }
        binding.btnStatistics.setOnClickListener {
            android.widget.Toast.makeText(this, "Statistics coming soon", android.widget.Toast.LENGTH_SHORT).show()
        }
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, StaffListActivity::class.java))
        }
    }

    private fun setupDate() {
        val fmt = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        binding.tvDateHeader.text = "🗓 Today: ${fmt.format(Date())}"
    }

    private fun setupRevenueChart() {
        val chart: BarChart = binding.barChartRevenue
        val values = floatArrayOf(800f, 1600f, 1400f, 2000f, 2400f, 3000f, 2800f)
        val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        val entries = ArrayList<BarEntry>()
        values.forEachIndexed { i, v -> entries.add(BarEntry(i.toFloat(), v)) }

        val dataSet = BarDataSet(entries, "").apply {
            color = Color.parseColor("#FF6B00")
            setDrawValues(false)
        }

        chart.apply {
            data = BarData(dataSet).also { it.barWidth = 0.6f }
            description.isEnabled = false
            legend.isEnabled = false
            setFitBars(true)
            setDrawGridBackground(false)
            setTouchEnabled(false)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(days)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textColor = Color.parseColor("#64748B")
                textSize = 11f
            }
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E2E8F0")
                textColor = Color.parseColor("#64748B")
                axisMinimum = 0f
                textSize = 11f
            }
            axisRight.isEnabled = false
            animateY(800)
        }
    }

    private fun setupOrdersChart() {
        val chart: LineChart = binding.lineChartOrders
        val values = floatArrayOf(6f, 18f, 10f, 22f, 14f)
        val times = arrayOf("9AM", "12PM", "3PM", "6PM", "9PM")

        val entries = ArrayList<Entry>()
        values.forEachIndexed { i, v -> entries.add(Entry(i.toFloat(), v)) }

        val dataSet = LineDataSet(entries, "").apply {
            color = Color.parseColor("#1A2540")
            lineWidth = 2.5f
            setDrawCircles(true)
            setCircleColor(Color.parseColor("#1A2540"))
            circleRadius = 4f
            setDrawValues(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(false)
        }

        chart.apply {
            data = LineData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setDrawGridBackground(false)
            setTouchEnabled(false)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(times)
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                textColor = Color.parseColor("#64748B")
                textSize = 11f
            }
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#E2E8F0")
                textColor = Color.parseColor("#64748B")
                axisMinimum = 0f
                textSize = 11f
            }
            axisRight.isEnabled = false
            animateX(800)
        }
    }

    private fun setupRecentActivity() {
        binding.rvRecentActivity.layoutManager = LinearLayoutManager(this)
        binding.rvRecentActivity.adapter = ActivityAdapter(recentActivity)
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

    inner class ActivityAdapter(
        private val items: List<ActivityItem>
    ) : RecyclerView.Adapter<ActivityAdapter.VH>() {

        inner class VH(view: View) : RecyclerView.ViewHolder(view) {
            val dot: View = view.findViewById(R.id.viewActivityDot)
            val tvDesc: TextView = view.findViewById(R.id.tvActivityDesc)
            val tvTime: TextView = view.findViewById(R.id.tvActivityTime)
            val tvPrice: TextView = view.findViewById(R.id.tvActivityPrice)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
            VH(layoutInflater.inflate(R.layout.item_activity_row, parent, false))

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = items[position]
            val dotDrawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor(item.dotColor))
            }
            holder.dot.background = dotDrawable
            holder.tvDesc.text = item.description
            holder.tvTime.text = item.timeAgo
            holder.tvPrice.text = "${"$"}${"%.2f".format(item.price)}"
        }
    }
}
