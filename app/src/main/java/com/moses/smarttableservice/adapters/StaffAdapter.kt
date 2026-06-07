package com.moses.smarttableservice.adapters

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.moses.smarttableservice.R
import com.moses.smarttableservice.databinding.ItemStaffBinding
import com.moses.smarttableservice.models.User
import com.moses.smarttableservice.utils.Constants

class StaffAdapter(
    private var staff: List<User>,
    private val onItemClick: (User) -> Unit
) : RecyclerView.Adapter<StaffAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemStaffBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStaffBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = staff[position]
        val ctx  = holder.itemView.context

        with(holder.binding) {
            tvStaffName.text  = user.name.ifEmpty { "Unknown" }
            tvStaffEmail.text = user.email
            tvInitial.text    = user.name.firstOrNull()?.uppercase() ?: "?"

            val (roleLabel, roleColor, roleColorLight) = when (user.role) {
                Constants.ROLE_MANAGER   -> Triple(
                    ctx.getString(R.string.role_manager),
                    ctx.getColor(R.color.colorManager),
                    ctx.getColor(R.color.colorManagerLight)
                )
                Constants.ROLE_WAITER    -> Triple(
                    ctx.getString(R.string.role_waiter),
                    ctx.getColor(R.color.colorWaiter),
                    ctx.getColor(R.color.colorWaiterLight)
                )
                else                     -> Triple(
                    ctx.getString(R.string.role_prep_cook),
                    ctx.getColor(R.color.colorKitchen),
                    ctx.getColor(R.color.colorKitchenLight)
                )
            }

            // Avatar circle
            val circle = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(roleColor)
            }
            tvInitial.background = circle

            // Role badge
            tvRole.text = roleLabel
            tvRole.setTextColor(roleColor)
            tvRole.backgroundTintList = ColorStateList.valueOf(roleColorLight)
            val badgeBg = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 20f
                setColor(roleColorLight)
            }
            tvRole.background = badgeBg
        }

        holder.itemView.setOnClickListener { onItemClick(user) }
    }

    override fun getItemCount() = staff.size

    fun updateList(newList: List<User>) {
        staff = newList
        notifyDataSetChanged()
    }
}
