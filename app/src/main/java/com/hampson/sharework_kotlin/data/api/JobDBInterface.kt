package com.hampson.sharework_kotlin.data.api

import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path

interface JobDBInterface {

    @GET("api/v1/jobs/{id}")
    fun getJobShow(@Path("id") job_id: Int): Single<Job>
}