package com.moses.smarttableservice.activities

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.moses.smarttableservice.R
import com.moses.smarttableservice.adapters.MenuItemAdapter
import com.moses.smarttableservice.models.MenuItem

class CustomerMenuActivity : AppCompatActivity() {

    private val tableNumber = 5

    // itemId -> quantity
    private val cart = HashMap<String, Int>()

    private lateinit var adapter: MenuItemAdapter
    private lateinit var cartBar: View
    private lateinit var tvCartInfo: TextView
    private lateinit var tvCartTotal: TextView

    // Sample data so the screen runs immediately.
    // Later, load these from Firestore (the "menuItems" collection) instead.
    private val menuItems = listOf(
        MenuItem("1", "Bruschetta", "Grilled bread, tomato, basil, olive oil", 8.50, "Starters"),
        MenuItem("2", "Caesar Salad", "Romaine, parmesan, croutons, house dressing", 9.00, "Starters"),
        MenuItem("3", "Margherita Pizza", "Tomato, mozzarella, fresh basil", 13.00, "Mains"),
        MenuItem("4", "Ribeye Steak", "12oz, mashed potatoes, grilled vegetables", 26.00, "Mains"),
        MenuItem("5", "Tiramisu", "Classic Italian dessert", 7.50, "Desserts")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_menu)

        findViewById<TextView>(R.id.tvTableNumber).text = "Table $tableNumber"

        cartBar = findViewById(R.id.cartBar)
        tvCartInfo = findViewById(R.id.tvCartInfo)
        tvCartTotal = findViewById(R.id.tvCartTotal)

        adapter = MenuItemAdapter(
            items = menuItems,
            quantityOf = { cart[it.itemId] ?: 0 },
            onAdd = { addToCart(it) },
            onRemove = { removeFromCart(it) }
        )

        findViewById<RecyclerView>(R.id.rvMenu).apply {
            layoutManager = LinearLayoutManager(this@CustomerMenuActivity)
            adapter = this@CustomerMenuActivity.adapter
        }

        cartBar.setOnClickListener { placeOrder() }

        updateCartBar()
    }

    private fun addToCart(item: MenuItem) {
        cart[item.itemId] = (cart[item.itemId] ?: 0) + 1
        adapter.notifyDataSetChanged()
        updateCartBar()
    }

    private fun removeFromCart(item: MenuItem) {
        val current = cart[item.itemId] ?: return
        if (current <= 1) cart.remove(item.itemId) else cart[item.itemId] = current - 1
        adapter.notifyDataSetChanged()
        updateCartBar()
    }

    private fun updateCartBar() {
        val totalItems = cart.values.sum()
        if (totalItems == 0) {
            cartBar.visibility = View.GONE
            return
        }
        val total = menuItems
            .filter { cart.containsKey(it.itemId) }
            .sumOf { it.price * (cart[it.itemId] ?: 0) }

        cartBar.visibility = View.VISIBLE
        tvCartInfo.text = "$totalItems item${if (totalItems == 1) "" else "s"} · View Order"
        tvCartTotal.text = "$%.2f".format(total)
    }

    private fun placeOrder() {
        if (cart.values.sum() == 0) return
        Toast.makeText(this, "Order placed for Table $tableNumber!", Toast.LENGTH_SHORT).show()
        cart.clear()
        adapter.notifyDataSetChanged()
        updateCartBar()
    }
}
