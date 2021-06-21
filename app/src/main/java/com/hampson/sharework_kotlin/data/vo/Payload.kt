package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class Payload(
    val jobList: List<Job>,
    @SerializedName("sms_auth")
    val smsAuth: SmsAuth,
    val meta: Meta
)