package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class Response(
    val page: Int,
    @SerializedName("response")
    val payload: Payload,
    @SerializedName("optional")
    val optional: Optional
)