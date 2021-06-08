package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName
import java.util.*

data class LocationFavorites (
    val id: Int,
    val user_id: Int,
    val location_name: String,
    val lat: Double,
    val lng: Double
)