package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("sms_auth")
    val smsAuth: SmsAuth,
    @SerializedName("user")
    val user: User,
    @SerializedName("location_favorites")
    val locationFavoritesList: List<LocationFavorites>,
    @SerializedName("location_favorite")
    val locationFavorites: LocationFavorites,

    val meta: Meta
)