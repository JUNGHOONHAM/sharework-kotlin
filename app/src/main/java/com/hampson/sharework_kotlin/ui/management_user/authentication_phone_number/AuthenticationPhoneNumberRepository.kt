package com.hampson.sharework_kotlin.ui.management_user.authentication_phone_number

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.AuthenticationPhoneNumberNetworkDataSource
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Meta
import com.hampson.sharework_kotlin.data.vo.Payload
import com.hampson.sharework_kotlin.data.vo.Response
import com.hampson.sharework_kotlin.data.vo.SmsAuth
import io.reactivex.disposables.CompositeDisposable

class AuthenticationPhoneNumberRepository (private val apiService : DBInterface) {
    lateinit var authenticationPhoneNumberNetworkDataSource: AuthenticationPhoneNumberNetworkDataSource

    fun sendPhoneNumber (compositeDisposable: CompositeDisposable, phoneNumber: String) : MutableLiveData<SmsAuth> {
        authenticationPhoneNumberNetworkDataSource = AuthenticationPhoneNumberNetworkDataSource(apiService, compositeDisposable)
        authenticationPhoneNumberNetworkDataSource.sendPhoneNumber(phoneNumber)

        return authenticationPhoneNumberNetworkDataSource.downlodedSmsAuthResponse
    }

    fun sendVerifiedNumber (compositeDisposable: CompositeDisposable, phoneNumber: String, token: String, verifiedNumber: String) : MutableLiveData<Response?> {
        authenticationPhoneNumberNetworkDataSource = AuthenticationPhoneNumberNetworkDataSource(apiService, compositeDisposable)
        authenticationPhoneNumberNetworkDataSource.sendVerifiedNumber(phoneNumber, token, verifiedNumber)

        return authenticationPhoneNumberNetworkDataSource.smsAuthMeta
    }

    fun getAuthenticationPhoneNumberNetworkState(): LiveData<NetworkState> {
        return authenticationPhoneNumberNetworkDataSource.networkState
    }
}