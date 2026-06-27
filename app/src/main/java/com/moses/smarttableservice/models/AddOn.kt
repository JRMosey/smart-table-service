package com.moses.smarttableservice.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddOn(
    var name: String = "",
    var price: Double = 0.0,
    var type: String = "extra", // "extra" or "remove"
    var isAvailable: Boolean = true
) : Parcelable