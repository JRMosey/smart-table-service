package com.moses.smarttableservice.utils

object Constants {
    const val ROLE_MANAGER = "manager"
    const val ROLE_WAITER = "waiter"
    const val ROLE_PREP_COOK = "prepCook"

    val ROLE_OPTIONS = listOf("Manager", "Waiter", "Prep Cook")

    val ROLE_DISPLAY = mapOf(
        ROLE_MANAGER  to "Manager",
        ROLE_WAITER   to "Waiter",
        ROLE_PREP_COOK to "Prep Cook"
    )

    val DISPLAY_TO_ROLE = mapOf(
        "Manager"   to ROLE_MANAGER,
        "Waiter"    to ROLE_WAITER,
        "Prep Cook" to ROLE_PREP_COOK
    )
}
