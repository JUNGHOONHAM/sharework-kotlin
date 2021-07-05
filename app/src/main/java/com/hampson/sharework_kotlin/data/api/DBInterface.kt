package com.hampson.sharework_kotlin.data.api

import androidx.fragment.app.FragmentActivity
import com.hampson.sharework_kotlin.data.vo.*
import com.hampson.sharework_kotlin.session.SessionManagement
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
    @GET("api/v2/users/jobs")
    fun getJobs(@Query("job_ids") job_id: String, @Query("page") page: Int, @Query("size") size: Int): Single<Response>

    @GET("api/v2/users/jobs/index_in_map")
    fun getOpenJobs(@Query("northeast_lat") northeast_lat: Double, @Query("northeast_lng") northeast_lng: Double,
                    @Query("southwest_lat") southwest_lat: Double, @Query("southwest_lng") southwest_lng: Double): Single<Response>

    @GET("api/v2/users/jobs/{id}")
    fun getJobShow(@Path("id") job_id: Int, @Query("user_id") user_id: Int): Single<Response>


    // application
    @POST("api/v2/users/job_applications")
    fun createApplication(@Body application: JobApplication): Single<Response>

    // location favorites
    @POST("api/v2/users/location_favorites")
    fun createLocationFavorites(@Body location_favorite: LocationFavorites): Single<Response>

    @DELETE("api/v2/users/location_favorites/{id}")
    fun deleteLocationFavorites(@Path("id") id: Int?): Single<Response>

    @GET("api/v2/users/location_favorites")
    fun getLocationFavorites(@Query("user_id") user_id: Int): Single<Response>
}