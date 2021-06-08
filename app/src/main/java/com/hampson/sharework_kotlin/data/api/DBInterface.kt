package com.hampson.sharework_kotlin.data.api

import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.Single
import retrofit2.http.*

interface DBInterface {

    // job
    @GET("api/v1/jobs")
    fun getJob(@Query("job_id") job_id: String, @Query("page") page: Int, @Query("size") size: Int): Single<JobResponse>

    @GET("api/v1/jobs/index_in_map")
    fun getOpenJobs(@Query("northeast_lat") northeast_lat: Double, @Query("northeast_lng") northeast_lng: Double,
                    @Query("southwest_lat") southwest_lat: Double, @Query("southwest_lng") southwest_lng: Double): Single<JobResponse>

    @GET("api/v1/jobs/{id}")
    fun getJobShow(@Path("id") job_id: Int): Single<Job>


    // location favorites
    @POST("location_favorites")
    fun createLocationFavorites(@Body location: LocationFavorites): Single<LocationFavoritesResponse>

    @GET("location_favorites")
    fun getLocationFavorites(@Query("user_id") user_id: Int): Single<LocationFavoritesResponse>
}