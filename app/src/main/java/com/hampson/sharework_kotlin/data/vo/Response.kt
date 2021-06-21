package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class Response(
    val page: Int,

    @SerializedName("optional")
    val optional: Optional,

    @SerializedName("status")
    val status: String,
    @SerializedName("payload")
    val payload: Payload,
    @SerializedName("message")
    val message: String,
    @SerializedName("meta")
    val meta: Meta
)