package com.hampson.sharework_kotlin.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class AuthenticationPhoneNumberNetworkDataSource (private val apiService : DBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedSmsAuthResponse = MutableLiveData<SmsAuth>()
    val downlodedSmsAuthResponse: MutableLiveData<SmsAuth>
        get() = _downloadedSmsAuthResponse

    private val _smsAuthMeta = MutableLiveData<Response?>()
    val smsAuthMeta: MutableLiveData<Response?>
        get() = _smsAuthMeta

    lateinit var data: SmsAuth

    fun sendPhoneNumber(phoneNumber: String) {
        _networkState.postValue(NetworkState.LOADING)   

        try {
            compositeDisposable.add(
                apiService.sendPhoneNumber(phoneNumber)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _downloadedSmsAuthResponse.postValue(it.payload.smsAuth)
                            _networkState.postValue(NetworkState.LOADED)
                        },
                        {
                            _networkState.postValue(NetworkState.ERROR)
                        }
                    )
            )
        } catch (e: Exception) {

        }
    }

    fun sendVerifiedNumber(phoneNumber: String, token: String, verifiedNumber: String) {
        try {
            compositeDisposable.add(
                apiService.sendVerifiedNumber(phoneNumber, token, verifiedNumber)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _smsAuthMeta.postValue(it)
                        },
                        {
                            _smsAuthMeta.postValue(null)
                        }
                    )
            )
        } catch (e: Exception) {

        }
    }
}