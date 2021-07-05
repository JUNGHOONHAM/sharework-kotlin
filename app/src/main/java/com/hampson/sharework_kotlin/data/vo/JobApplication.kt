package com.hampson.sharework_kotlin.data.vo

data class JobApplication(
    val id: Int?,
    val job_id: Int,
    val app_start: String?,
    val app_end: String?,
    val distance: String?,
    val status: String,
    val user_id: Int,

    val user_checklists: List<UserChecklist>
)