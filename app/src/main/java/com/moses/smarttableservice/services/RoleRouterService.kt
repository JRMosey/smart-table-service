package com.moses.smarttableservice.services

import android.content.Context
import android.content.Intent
import com.moses.smarttableservice.activities.KitchenDashboardActivity
import com.moses.smarttableservice.activities.ManagerDashboardActivity
import com.moses.smarttableservice.activities.WaiterDashboardActivity

object RoleRouterService {

    fun routeUser(context: Context, role: String) {
        val intent = when (role) {
            "manager" -> Intent(context, ManagerDashboardActivity::class.java)
            "waiter" -> Intent(context, WaiterDashboardActivity::class.java)
            "prepCook" -> Intent(context, KitchenDashboardActivity::class.java)
            else -> Intent(context, ManagerDashboardActivity::class.java)
        }

        context.startActivity(intent)
    }
}