package com.hampson.sharework_kotlin.data.vo

data class UserJobRateReview(
    val id: Int,
    val contents: String,
    val sender: User,
    val rating: Float,

    val review_base_count_list: HashMap<String, Int>,
    val review_rate_sum: Float
)