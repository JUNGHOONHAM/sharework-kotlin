package com.hampson.sharework_kotlin.data.vo

import com.google.gson.annotations.SerializedName

data class Payload(
    @SerializedName("sms_auth")
    val smsAuth: SmsAuth,
    @SerializedName("user")
    val user: User,

    @SerializedName("location_favorites")
    val locationFavoritesList: List<LocationFavorites>,
    @SerializedName("location_favorite")
    val locationFavorites: LocationFavorites,

    @SerializedName("jobs")
    val jobList: List<Job>,
    @SerializedName("job")
    val job: Job,

    @SerializedName("job_applications")
    val jobApplicationlist: List<JobApplication>,
    @SerializedName("job_application")
    val jobApplication: JobApplication,

    @SerializedName("meta")
    val meta: Meta
)