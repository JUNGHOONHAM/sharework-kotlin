package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class UserInfoUpdateNetworkDataSource (private val apiService : DBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedUserResponse = MutableLiveData<User>()
    val downloadedUserResponse: MutableLiveData<User>
        get() = _downloadedUserResponse

    private val _updateResult = MutableLiveData<Boolean>()
    val updateResult: MutableLiveData<Boolean>
        get() = _updateResult

    fun getUser(userId: Int) {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService.getUser(userId)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _downloadedUserResponse.postValue(it.payload.user)
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

    fun updateUser(userId: Int, user: User) {
        try {
            compositeDisposable.add(
                apiService.updateUser(userId, user)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _updateResult.postValue(true)
                        },
                        {
                            _updateResult.postValue(false)
                        }
                    )
            )
        } catch (e: Exception) {
            _updateResult.postValue(false)
        }
    }
}