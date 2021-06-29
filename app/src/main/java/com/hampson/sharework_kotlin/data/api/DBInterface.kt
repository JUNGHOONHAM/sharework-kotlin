package com.hampson.sharework_kotlin.data.api

import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.Single
import retrofit2.http.*

interface DBInterface {

    // sms_auth
    @POST("api/v2/sms_auth/send_sms")
    fun sendPhoneNumber(@Query("phone_number") phoneNumber: String): Single<Response>

    @POST("api/v2/sms_auth/verified")
    fun sendVerifiedNumber(@Query("phone_number") phoneNumber: String, @Query("token") token: String, @Query("verified_number") verifiedNumber: String): Single<Response>


    // user
    @POST("api/v2/registrations")
    fun createUser(@Body user: User): Single<Response>


    // job
    @GET("api/v1/jobs")
    fun getJob(@Query("job_id") job_id: String, @Query("page") page: Int, @Query("size") size: Int): Single<JobResponse>

    @GET("api/v1/jobs/index_in_map")
    fun getOpenJobs(@Query("northeast_lat") northeast_lat: Double, @Query("northeast_lng") northeast_lng: Double,
                    @Query("southwest_lat") southwest_lat: Double, @Query("southwest_lng") southwest_lng: Double): Single<JobResponse>

    @GET("api/v1/jobs/{id}")
    fun getJobShow(@Path("id") job_id: Int): Single<Job>


    // location favorites
    @POST("api/v2/users/location_favorites")
    fun createLocationFavorites(@Body location_favorite: LocationFavorites): Single<Response>

    @DELETE("api/v2/users/location_favorites/{id}")
    fun deleteLocationFavorites(@Path("id") id: Int?): Single<Response>

    @GET("location_favorites")
    fun getLocationFavorites(@Query("user_id") user_id: Int): Single<LocationFavoritesResponse>
}