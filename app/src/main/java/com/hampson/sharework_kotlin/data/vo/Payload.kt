package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("payload")
    val jobList: List<Job>
)