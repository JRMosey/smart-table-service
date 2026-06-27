package com.moses.smarttableservice.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.moses.smarttableservice.R
import com.moses.smarttableservice.models.RestaurantTable

class TableAdapter(
    private var tables: List<RestaurantTable>,
    private val onTableClick: (RestaurantTable) -> Unit,
    private val onTableLongClick: (RestaurantTable) -> Unit
) : RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    class TableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTableName: TextView = view.findViewById(R.id.tvTableName)
        val tvTableDetails: TextView = view.findViewById(R.id.tvTableDetails)
        val tvTableStatus: TextView = view.findViewById(R.id.tvTableStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_table, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val table = tables[position]
        holder.tvTableName.text = table.name.ifEmpty { "Table ${table.tableNumber}" }
        holder.tvTableDetails.text = "Capacity: ${table.capacity}"
        holder.tvTableStatus.text = table.status.replaceFirstChar { it.uppercase() }
        
        val statusColor = when (table.status) {
            "available" -> "#10B981" // status_success
            "occupied" -> "#EF4444"  // status_danger
            "reserved" -> "#F59E0B"  // status_warning
            "cleaning" -> "#3B82F6"  // status_info
            else -> "#64748B"
        }
        holder.tvTableStatus.setBackgroundColor(Color.parseColor(statusColor))

        holder.itemView.setOnClickListener { onTableClick(table) }
        holder.itemView.setOnLongClickListener {
            onTableLongClick(table)
            true
        }
    }

    override fun getItemCount() = tables.size

    fun updateTables(newTables: List<RestaurantTable>) {
        tables = newTables
        notifyDataSetChanged()
    }
}