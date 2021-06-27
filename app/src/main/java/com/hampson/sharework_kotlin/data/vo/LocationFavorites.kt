package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class LocationFavoritesResponse(
    @SerializedName("response")
    val payload: LocationFavoritesPayload,
    @SerializedName("optional")
    val optional: Optional
)

data class LocationFavoritesPayload(
    @SerializedName("payload")
    val locationFavoritesList: List<LocationFavorites>
)

data class LocationFavorites(
    val id: Int?,
    val user_id: Int,
    val location_name: String,
    val lat: Double,
    val lng: Double
)