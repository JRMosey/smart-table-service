package com.moses.smarttableservice.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moses.smarttableservice.R
import com.moses.smarttableservice.adapters.MenuItemAdapter
import com.moses.smarttableservice.models.AddOn
import com.moses.smarttableservice.models.MenuItem
import com.moses.smarttableservice.repositories.MenuRepository

class ManageMenuActivity : AppCompatActivity() {

    private val menuRepository = MenuRepository()
    private lateinit var rvMenuItems: RecyclerView
    private lateinit var btnAddMenuItem: Button
    private lateinit var menuAdapter: MenuItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_menu)

        rvMenuItems = findViewById(R.id.rvMenuItems)
        btnAddMenuItem = findViewById(R.id.btnAddMenuItem)

        setupRecyclerView()

        btnAddMenuItem.setOnClickListener {
            showMenuItemDialog(null)
        }

        loadMenuItems()
    }

    private fun setupRecyclerView() {
        menuAdapter = MenuItemAdapter(
            items = emptyList(),
            onItemClick = { item -> showMenuItemDialog(item) },
            onAvailabilityChanged = { item, isAvailable ->
                val updatedItem = item.copy(isAvailable = isAvailable)
                menuRepository.updateMenuItem(updatedItem, { /* Success */ }, { e -> 
                    Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
                })
            }
        )
        rvMenuItems.layoutManager = LinearLayoutManager(this)
        rvMenuItems.adapter = menuAdapter
    }

    private fun loadMenuItems() {
        menuRepository.getAllMenuItems(
            onSuccess = { items ->
                menuAdapter.updateItems(items.sortedBy { it.category })
            },
            onFailure = { e ->
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showMenuItemDialog(item: MenuItem?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_menu_item, null)
        val etName = dialogView.findViewById<EditText>(R.id.etItemName)
        val etDesc = dialogView.findViewById<EditText>(R.id.etItemDescription)
        val etPrice = dialogView.findViewById<EditText>(R.id.etItemPrice)
        val etCategory = dialogView.findViewById<EditText>(R.id.etItemCategory)
        val addOnsContainer = dialogView.findViewById<LinearLayout>(R.id.addOnsContainer)
        val btnAddAddOn = dialogView.findViewById<Button>(R.id.btnAddAddOn)

        val currentAddOns = mutableListOf<AddOn>()

        fun addAddOnView(addOn: AddOn?) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_addon_edit, null)
            val etAddOnName = view.findViewById<EditText>(R.id.etAddOnName)
            val etAddOnPrice = view.findViewById<EditText>(R.id.etAddOnPrice)
            val btnRemove = view.findViewById<Button>(R.id.btnRemoveAddOn)

            addOn?.let {
                etAddOnName.setText(it.name)
                etAddOnPrice.setText(it.price.toString())
            }

            btnRemove.setOnClickListener {
                addOnsContainer.removeView(view)
            }
            addOnsContainer.addView(view)
        }

        item?.let {
            etName.setText(it.name)
            etDesc.setText(it.description)
            etPrice.setText(it.price.toString())
            etCategory.setText(it.category)
            it.addOns.forEach { addOn -> addAddOnView(addOn) }
        }

        btnAddAddOn.setOnClickListener {
            addAddOnView(null)
        }

        AlertDialog.Builder(this)
            .setTitle(if (item == null) "Add Dish" else "Edit Dish")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val name = etName.text.toString()
                val desc = etDesc.text.toString()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val category = etCategory.text.toString()

                val finalAddOns = mutableListOf<AddOn>()
                for (i in 0 until addOnsContainer.childCount) {
                    val child = addOnsContainer.getChildAt(i)
                    val aName = child.findViewById<EditText>(R.id.etAddOnName).text.toString()
                    val aPrice = child.findViewById<EditText>(R.id.etAddOnPrice).text.toString().toDoubleOrNull() ?: 0.0
                    if (aName.isNotEmpty()) {
                        finalAddOns.add(AddOn(name = aName, price = aPrice, type = if (aPrice >= 0) "extra" else "remove"))
                    }
                }

                if (name.isNotEmpty()) {
                    val newItem = item?.copy(
                        name = name,
                        description = desc,
                        price = price,
                        category = category,
                        addOns = finalAddOns
                    ) ?: MenuItem(
                        name = name,
                        description = desc,
                        price = price,
                        category = category,
                        isAvailable = true,
                        addOns = finalAddOns
                    )

                    if (item == null) {
                        menuRepository.addMenuItem(newItem, { loadMenuItems() }, { e -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show() })
                    } else {
                        menuRepository.updateMenuItem(newItem, { loadMenuItems() }, { e -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show() })
                    }
                }
            }
            .setNegativeButton("Cancel", null)
            .setNeutralButton(if (item != null) "Delete" else null) { _, _ ->
                item?.let {
                    menuRepository.deleteMenuItem(it.itemId, { loadMenuItems() }, { e -> Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show() })
                }
            }
            .show()
    }
}