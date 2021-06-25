package com.hampson.sharework_kotlin.ui.management_user.authentication_phone_number

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.AuthenticationPhoneNumberNetworkDataSource
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.SmsAuth
import io.reactivex.disposables.CompositeDisposable

class AuthenticationPhoneNumberRepository (private val apiService : DBInterface) {
    lateinit var authenticationPhoneNumberNetworkDataSource: AuthenticationPhoneNumberNetworkDataSource

    fun sendPhoneNumber (compositeDisposable: CompositeDisposable, phoneNumber: String) : MutableLiveData<SmsAuth> {
        authenticationPhoneNumberNetworkDataSource = AuthenticationPhoneNumberNetworkDataSource(apiService, compositeDisposable)
        authenticationPhoneNumberNetworkDataSource.sendPhoneNumber(phoneNumber)

        return authenticationPhoneNumberNetworkDataSource.downlodedJobResponse
    }

    fun getAuthenticationPhoneNumberNetworkState(): LiveData<NetworkState> {
        return authenticationPhoneNumberNetworkDataSource.networkState
    }
}