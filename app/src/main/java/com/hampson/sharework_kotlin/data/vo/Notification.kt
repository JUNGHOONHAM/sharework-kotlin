package com.hampson.sharework_kotlin.data.vo

data class Notification(
    val id: Int,
    val receiver_id: Int,
    val sender_id: Int,
    val user_type: String,
    val not_type: String,
    val job: Job,
    val sender: User
)