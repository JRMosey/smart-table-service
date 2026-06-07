package com.moses.smarttableservice.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moses.smarttableservice.R
import com.moses.smarttableservice.models.MenuItem


class MenuItemAdapter(
    private val items: List<MenuItem>,
    private val quantityOf: (MenuItem) -> Int,
    private val onAdd: (MenuItem) -> Unit,
    private val onRemove: (MenuItem) -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val placeholder: TextView = view.findViewById(R.id.tvPlaceholder)
        val name: TextView = view.findViewById(R.id.tvName)
        val description: TextView = view.findViewById(R.id.tvDescription)
        val price: TextView = view.findViewById(R.id.tvPrice)
        val btnMinus: TextView = view.findViewById(R.id.btnMinus)
        val tvQty: TextView = view.findViewById(R.id.tvQty)
        val btnPlus: TextView = view.findViewById(R.id.btnPlus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_menu_card, parent, false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = items[position]

        holder.placeholder.text = item.name.firstOrNull()?.uppercase() ?: "?"
        holder.name.text = item.name
        holder.price.text = "$%.2f".format(item.price)

        if (item.description.isNullOrBlank()) {
            holder.description.visibility = View.GONE
        } else {
            holder.description.visibility = View.VISIBLE
            holder.description.text = item.description
        }

        val qty = quantityOf(item)
        if (qty > 0) {
            holder.btnMinus.visibility = View.VISIBLE
            holder.tvQty.visibility = View.VISIBLE
            holder.tvQty.text = qty.toString()
        } else {
            holder.btnMinus.visibility = View.GONE
            holder.tvQty.visibility = View.GONE
        }

        holder.btnPlus.setOnClickListener { onAdd(item) }
        holder.btnMinus.setOnClickListener { onRemove(item) }
    }
}
