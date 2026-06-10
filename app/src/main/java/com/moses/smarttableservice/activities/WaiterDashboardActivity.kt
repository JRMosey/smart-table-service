package com.moses.smarttableservice.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.moses.smarttableservice.R
import com.moses.smarttableservice.models.RestaurantTable
import com.moses.smarttableservice.repositories.TableRepository

class WaiterDashboardActivity : AppCompatActivity() {

    private val tableRepository = TableRepository()

    private lateinit var tableGrid: GridLayout
    private lateinit var btnCreateOrder: Button

    private var selectedTable: RestaurantTable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_waiter_dashboard)

        tableGrid = findViewById(R.id.tableGrid)
        btnCreateOrder = findViewById(R.id.btnCreateOrder)

        btnCreateOrder.setOnClickListener {

            if (selectedTable == null) {

                Toast.makeText(
                    this,
                    "Please select a table first",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

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
        }

        loadTables()
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

                Toast.makeText(
                    this,
                    exception.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun createTableView(table: RestaurantTable): TextView {

        val tableView = TextView(this)

        tableView.text =
            "Table ${table.tableNumber}\n${table.status}"

        tableView.gravity = Gravity.CENTER
        tableView.textSize = 16f
        tableView.setTextColor(Color.BLACK)
        tableView.setPadding(12, 12, 12, 12)

        val params = GridLayout.LayoutParams()

        params.width = 0
        params.height = 160
        params.columnSpec =
            GridLayout.spec(GridLayout.UNDEFINED, 1f)

        params.setMargins(
            8,
            8,
            8,
            8
        )

        tableView.layoutParams = params

        tableView.setBackgroundColor(
            getStatusColor(table.status)
        )

        tableView.setOnClickListener {

            selectedTable = table

            Toast.makeText(
                this,
                "Table ${table.tableNumber} selected",
                Toast.LENGTH_SHORT
            ).show()
        }

        return tableView
    }

    private fun getStatusColor(status: String): Int {

        return when (status) {

            "available" ->
                Color.parseColor("#D1FAE5")

            "occupied" ->
                Color.parseColor("#FEE2E2")

            "reserved" ->
                Color.parseColor("#FEF3C7")

            "cleaning" ->
                Color.parseColor("#DBEAFE")

            else ->
                Color.LTGRAY
        }
    }
}