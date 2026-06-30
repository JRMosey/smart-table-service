package com.moses.smarttableservice.activities

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.moses.smarttableservice.R
import com.moses.smarttableservice.models.MenuItem
import com.moses.smarttableservice.models.Order
import com.moses.smarttableservice.models.OrderItem
import com.moses.smarttableservice.repositories.MenuRepository
import com.moses.smarttableservice.repositories.OrderRepository
import com.moses.smarttableservice.repositories.TableRepository

class CreateOrderActivity : AppCompatActivity() {

    private val menuRepository = MenuRepository()
    private val orderRepository = OrderRepository()
    private val tableRepository = TableRepository()

    private lateinit var tvSelectedTable: TextView
    private lateinit var menuItemsContainer: LinearLayout
    private lateinit var tvOrderTotal: TextView
    private lateinit var btnSubmitOrder: Button

    private val selectedItems = mutableMapOf<MenuItem, Int>()

    private var tableId: String = ""
    private var tableNumber: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_order)

        tvSelectedTable = findViewById(R.id.tvSelectedTable)
        menuItemsContainer = findViewById(R.id.menuItemsContainer)
        tvOrderTotal = findViewById(R.id.tvOrderTotal)
        btnSubmitOrder = findViewById(R.id.btnSubmitOrder)

        tableId = intent.getStringExtra("tableId") ?: ""
        tableNumber = intent.getIntExtra("tableNumber", 0)

        tvSelectedTable.text = "Create Order - Table $tableNumber"

        btnSubmitOrder.setOnClickListener {
            submitOrder()
        }

        loadMenuItems()
    }

    private fun loadMenuItems() {
        menuRepository.getAvailableMenuItems(
            onSuccess = { items ->
                menuItemsContainer.removeAllViews()

                if (items.isEmpty()) {
                    Toast.makeText(this, "No menu items found", Toast.LENGTH_SHORT).show()
                }

                items.forEach { item ->
                    menuItemsContainer.addView(createMenuItemView(item))
                }
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun createMenuItemView(item: MenuItem): LinearLayout {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.HORIZONTAL
        layout.gravity = Gravity.CENTER_VERTICAL
        layout.setPadding(16, 16, 16, 16)
        layout.setBackgroundColor(0xFFFFFFFF.toInt())

        val imageSize = (64 * resources.displayMetrics.density).toInt()
        val ivImage = ImageView(this)
        ivImage.layoutParams = LinearLayout.LayoutParams(imageSize, imageSize).also {
            it.marginEnd = (12 * resources.displayMetrics.density).toInt()
        }
        ivImage.scaleType = ImageView.ScaleType.CENTER_CROP
        ivImage.setBackgroundColor(0xFFF1F5F9.toInt())
        if (item.imageUrl.isNotEmpty()) {
            Glide.with(this).load(item.imageUrl).centerCrop()
                .placeholder(R.drawable.ic_dish_placeholder).into(ivImage)
        } else {
            ivImage.setImageResource(R.drawable.ic_dish_placeholder)
        }
        layout.addView(ivImage)

        val info = TextView(this)
        info.text = "${item.name}\n$${"%.2f".format(item.price)}"
        info.textSize = 16f
        info.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)

        val btnMinus = Button(this)
        btnMinus.text = "-"

        val quantityText = TextView(this)
        quantityText.text = "0"
        quantityText.gravity = Gravity.CENTER
        quantityText.textSize = 16f
        quantityText.width = 60

        val btnPlus = Button(this)
        btnPlus.text = "+"

        btnPlus.setOnClickListener {
            val currentQuantity = selectedItems[item] ?: 0
            selectedItems[item] = currentQuantity + 1
            quantityText.text = selectedItems[item].toString()
            updateTotal()
        }

        btnMinus.setOnClickListener {
            val currentQuantity = selectedItems[item] ?: 0

            if (currentQuantity > 1) {
                selectedItems[item] = currentQuantity - 1
            } else {
                selectedItems.remove(item)
            }

            quantityText.text = (selectedItems[item] ?: 0).toString()
            updateTotal()
        }

        layout.addView(info)
        layout.addView(btnMinus)
        layout.addView(quantityText)
        layout.addView(btnPlus)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 12)
        layout.layoutParams = params

        return layout
    }

    private fun updateTotal() {
        val subtotal = selectedItems.entries.sumOf { entry ->
            entry.key.price * entry.value
        }

        tvOrderTotal.text = "Total: $${"%.2f".format(subtotal)}"
    }

    private fun submitOrder() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show()
            return
        }

        val subtotal = selectedItems.entries.sumOf { entry ->
            entry.key.price * entry.value
        }

        val taxAmount = subtotal * 0.15
        val total = subtotal + taxAmount

        val orderItems = selectedItems.map { entry ->
            OrderItem(
                itemId = entry.key.itemId,
                name = entry.key.name,
                quantity = entry.value,
                unitPrice = entry.key.price,
                kitchenStatus = "pending"
            )
        }

        val waiterId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        val order = Order(
            tableId = tableId,
            waiterId = waiterId,
            orderType = "dine_in",
            status = "pending",
            items = orderItems,
            subtotal = subtotal,
            taxAmount = taxAmount,
            discountAmount = 0.0,
            total = total,
            notes = ""
        )

        orderRepository.createOrder(
            order,
            onSuccess = {
                tableRepository.updateTableStatus(
                    tableId = tableId,
                    status = "occupied",
                    onSuccess = {
                        Toast.makeText(this, "Order sent to kitchen", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onFailure = { exception ->
                        Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
                    }
                )
            },
            onFailure = { exception ->
                Toast.makeText(this, exception.message, Toast.LENGTH_SHORT).show()
            }
        )
    }
}