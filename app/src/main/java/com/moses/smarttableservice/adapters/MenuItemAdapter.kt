package com.moses.smarttableservice.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.moses.smarttableservice.R
import com.moses.smarttableservice.models.MenuItem

class MenuItemAdapter(
    private var items: List<MenuItem>,
    private val onItemClick: (MenuItem) -> Unit,
    private val onAvailabilityChanged: (MenuItem, Boolean) -> Unit
) : RecyclerView.Adapter<MenuItemAdapter.MenuViewHolder>() {

    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivMenuItemImage: ImageView = view.findViewById(R.id.ivMenuItemImage)
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvItemPrice: TextView = view.findViewById(R.id.tvItemPrice)
        val tvItemCategory: TextView = view.findViewById(R.id.tvItemCategory)
        val switchAvailable: SwitchCompat = view.findViewById(R.id.switchAvailable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_menu_manage, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = items[position]
        holder.tvItemName.text = item.name
        holder.tvItemPrice.text = "$${"%.2f".format(item.price)}"
        holder.tvItemCategory.text = item.category

        if (item.imageUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(item.imageUrl)
                .centerCrop()
                .placeholder(R.drawable.ic_dish_placeholder)
                .into(holder.ivMenuItemImage)
        } else {
            holder.ivMenuItemImage.setImageResource(R.drawable.ic_dish_placeholder)
        }

        holder.switchAvailable.setOnCheckedChangeListener(null)
        holder.switchAvailable.isChecked = item.isAvailable
        holder.switchAvailable.setOnCheckedChangeListener { _, isChecked ->
            onAvailabilityChanged(item, isChecked)
        }

        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<MenuItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}