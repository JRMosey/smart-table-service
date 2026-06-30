package com.moses.smarttableservice.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moses.smarttableservice.R
import com.moses.smarttableservice.adapters.TableAdapter
import com.moses.smarttableservice.models.RestaurantTable
import com.moses.smarttableservice.repositories.TableRepository

class ManageTablesActivity : AppCompatActivity() {

    private val tableRepository = TableRepository()

    private lateinit var rvTables: RecyclerView
    private lateinit var btnAddTable: Button
    private lateinit var tableAdapter: TableAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_tables)

        rvTables = findViewById(R.id.rvTables)
        btnAddTable = findViewById(R.id.btnAddTable)

        setupRecyclerView()

        btnAddTable.setOnClickListener {
            showTableDialog(null)
        }

        loadTables()
    }

    private fun setupRecyclerView() {
        tableAdapter = TableAdapter(
            tables = emptyList(),
            onTableClick = { table ->
                showTableDialog(table)
            },
            onTableLongClick = { table ->
                showDeleteConfirmation(table)
            }
        )

        rvTables.layoutManager = LinearLayoutManager(this)
        rvTables.adapter = tableAdapter
    }

    private fun loadTables() {
        tableRepository.getTables(
            onSuccess = { tables ->
                tableAdapter.updateTables(
                    tables.sortedBy { it.tableNumber }
                )
            },
            onFailure = { exception ->
                Toast.makeText(
                    this,
                    "Error: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun showTableDialog(table: RestaurantTable?) {
        val dialogView = LayoutInflater.from(this)
            .inflate(R.layout.dialog_add_table, null)

        val etNumber = dialogView.findViewById<EditText>(R.id.etTableNumber)
        val etName = dialogView.findViewById<EditText>(R.id.etTableName)
        val etCapacity = dialogView.findViewById<EditText>(R.id.etTableCapacity)

        table?.let {
            etNumber.setText(it.tableNumber.toString())
            etName.setText(it.name)
            etCapacity.setText(it.capacity.toString())
        }

        AlertDialog.Builder(this)
            .setTitle(if (table == null) "Add Table" else "Edit Table")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->

                val number = etNumber.text.toString().toIntOrNull() ?: 0
                val inputName = etName.text.toString().trim()
                val capacity = etCapacity.text.toString().toIntOrNull() ?: 0

                if (number <= 0) {
                    Toast.makeText(
                        this,
                        "Please enter a valid table number",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                if (capacity <= 0) {
                    Toast.makeText(
                        this,
                        "Please enter a valid capacity",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setPositiveButton
                }

                val tableId = "table_$number"
                val tableName =
                    if (inputName.isNotBlank()) inputName else "Table $number"

                val newTable = table?.copy(
                    tableId = tableId,
                    tableNumber = number,
                    name = tableName,
                    capacity = capacity
                ) ?: RestaurantTable(
                    tableId = tableId,
                    tableNumber = number,
                    name = tableName,
                    capacity = capacity,
                    status = "available",
                    currentOrderId = "",
                    assignedWaiterId = ""
                )

                if (table == null) {
                    tableRepository.addTable(
                        table = newTable,
                        onSuccess = {
                            Toast.makeText(
                                this,
                                "Table added",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadTables()
                        },
                        onFailure = { exception ->
                            Toast.makeText(
                                this,
                                exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                } else {
                    tableRepository.updateTable(
                        table = newTable,
                        onSuccess = {
                            Toast.makeText(
                                this,
                                "Table updated",
                                Toast.LENGTH_SHORT
                            ).show()
                            loadTables()
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
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteConfirmation(table: RestaurantTable) {
        AlertDialog.Builder(this)
            .setTitle("Delete Table")
            .setMessage("Are you sure you want to delete ${table.name}?")
            .setPositiveButton("Delete") { _, _ ->
                tableRepository.deleteTable(
                    tableId = table.tableId,
                    onSuccess = {
                        Toast.makeText(
                            this,
                            "Table deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadTables()
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
            .setNegativeButton("Cancel", null)
            .show()
    }
}