package com.hampson.sharework_kotlin.data.api

import androidx.fragment.app.FragmentActivity
import com.hampson.sharework_kotlin.data.vo.*
import com.hampson.sharework_kotlin.session.SessionManagement
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    @GET("api/v2/users/users/{id}")
    fun getUser(@Path("id") user_id: Int): Single<Response>

    @PATCH("api/v2/users/users/{id}")
    fun updateUser(@Path("id") user_id: Int, @Body user: User): Single<Response>

    @Multipart
    @POST("api/v1/profile/upsert")
    fun updateProfileImage(@Part img_file: MultipartBody.Part, @Part("user_id") user_id: RequestBody): Single<Response>


    // job
    @GET("api/v2/users/jobs")
    fun getJobs(@Query("job_ids") job_id: String, @Query("page") page: Int, @Query("size") size: Int): Single<Response>

    @GET("api/v2/users/jobs/index_in_map")
    fun getOpenJobs(@Query("northeast_lat") northeast_lat: Double, @Query("northeast_lng") northeast_lng: Double,
                    @Query("southwest_lat") southwest_lat: Double, @Query("southwest_lng") southwest_lng: Double): Single<Response>

    @GET("api/v2/users/jobs/{id}")
    fun getJobShow(@Path("id") job_id: Int): Single<Response>


    // application
    @POST("api/v2/users/job_applications")
    fun createApplication(@Body application: JobApplication): Single<Response>

    @GET("api/v2/users/job_applications/index_payment")
    fun getApplications(@Query("user_id") user_id: Int, @Query("start_date") start_date: String, @Query("end_date") end_date: String,
                        @Query("page") page: Int, @Query("size") size: Int): Single<Response>

    @GET("api/v2/users/job_applications/show_payment")
    fun getPaymentMeta(@Query("start_date") start_date: String, @Query("end_date") end_date: String): Single<Response>


    // location favorites
    @POST("api/v2/users/location_favorites")
    fun createLocationFavorites(@Body location_favorite: LocationFavorites): Single<Response>

    @DELETE("api/v2/users/location_favorites/{id}")
    fun deleteLocationFavorites(@Path("id") id: Int?): Single<Response>

    @GET("api/v2/users/location_favorites")
    fun getLocationFavorites(@Query("user_id") user_id: Int): Single<Response>


    // tag
    @GET("api/v2/users/tags/index_user_tag_history")
    fun getUserTagHistory(@Query("user_id") user_id: Int, @Query("subject") subject: String): Single<Response>
}