package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class Meta(
    val is_valid_number: Boolean,
    val is_already_auth_phone_number: Boolean,
    val applied_check: Boolean,

    val page: Any,
    val size: Any,
    val time: Any,
    val total_page: Int
)