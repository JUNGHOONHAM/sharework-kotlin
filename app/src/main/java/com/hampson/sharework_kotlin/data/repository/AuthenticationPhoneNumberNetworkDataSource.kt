package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.Response
import com.hampson.sharework_kotlin.data.vo.SmsAuth
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception
import java.util.logging.Handler

class AuthenticationPhoneNumberNetworkDataSource (private val apiService : DBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedJobResponse = MutableLiveData<SmsAuth>()
    val downlodedJobResponse: MutableLiveData<SmsAuth>
        get() = _downloadedJobResponse

    lateinit var data: SmsAuth

    fun sendPhoneNumber(phoneNumber: String) {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService.sendPhoneNumber(phoneNumber)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            //_downloadedJobResponse.postValue(it.payload.smsAuth)
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
}