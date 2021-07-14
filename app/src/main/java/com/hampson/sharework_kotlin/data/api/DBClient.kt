package com.hampson.sharework_kotlin.data.api

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.hampson.sharework_kotlin.session.SessionManagement
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

const val BASE_URL = "http://3.34.231.112"

const val FIRST_PAGE = 1
const val POST_PER_PAGE = 5



object DBClient {
    fun getClient(context: Context): DBInterface {
        val sessionManagement = SessionManagement(context)
        val userId = sessionManagement.getSessionID()

        val requestInterceptor = Interceptor { chain ->
            val url : HttpUrl = chain.request()
                .url()
                .newBuilder()
                .build()

            val request : Request = chain.request()
                .newBuilder()
                .url(url)
                .addHeader("user_id", userId.toString())
                .build()

            return@Interceptor chain.proceed(request)
        }

        val okHttpClient : OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DBInterface::class.java)
    }
}