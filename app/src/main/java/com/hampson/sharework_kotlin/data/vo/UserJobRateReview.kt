package com.hampson.sharework_kotlin.data.vo

data class UserJobRateReview(
    val review_base_count_list: HashMap<String, Int>,
    val review_rate_sum: Double
)