package com.hampson.sharework_kotlin.ui.management_user

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.data.vo.SmsAuth
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class AuthenticationPhoneNumberViewModel (private val authenticationPhoneNumberRepository : AuthenticationPhoneNumberRepository,
                                          private val apiService : DBInterface) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val liveData: MutableLiveData<SmsAuth> = MutableLiveData()

    fun getSmsAuth(): LiveData<SmsAuth> {
        return liveData
    }

    val networkState : LiveData<NetworkState> by lazy {
        authenticationPhoneNumberRepository.getAuthenticationPhoneNumberNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun sendPhoneNumber(phoneNumber: String) {
        try {
            compositeDisposable.add(
                apiService.sendPhoneNumber(phoneNumber)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            liveData.postValue(it.payload.smsAuth)
                        },
                        {

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
                            Log.d("SEJFKL", it.toString())
                            if (it.status == "success") {
                                if (it.payload.meta.is_already_auth_phone_number) {
                                    Log.d("response success", "true")
                                } else {
                                    Log.d("response success", "false")
                                }
                            } else {
                                Log.d("response false", it.message)
                            }
                        },
                        {

                        }
                    )
            )
        } catch (e: Exception) {

        }
    }
}