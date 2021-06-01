package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class JobResponse (
    val page: Int,
    @SerializedName("response")
    val payload: Payload,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)