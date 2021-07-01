package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class Response(
    val page: Int,

    @SerializedName("status")
    val status: String,
    @SerializedName("payload")
    val payload: Payload,
    @SerializedName("message")
    val message: String
)