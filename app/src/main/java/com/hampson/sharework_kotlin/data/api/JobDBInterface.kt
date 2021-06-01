package com.hampson.sharework_kotlin.data.api

import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JobDBInterface {

    @GET("api/v1/jobs")
    fun getJob(@Query("job_id") job_id: String, @Query("page") page: Int, @Query("size") size: Int): Single<JobResponse>

    @GET("api/v1/jobs/{id}")
    fun getJobShow(@Path("id") job_id: Int): Single<Job>
}