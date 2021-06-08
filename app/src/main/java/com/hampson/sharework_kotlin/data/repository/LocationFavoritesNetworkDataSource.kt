package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.Response
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class LocationFavoritesNetworkDataSource (private val apiService : DBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedJobResponse = MutableLiveData<Response>()
    val downlodedJobResponse: LiveData<Response>
        get() = _downloadedJobResponse

    fun fetchLocationFavorites(userId: Int) {
        _networkState.postValue(NetworkState.LOADING)
        Log.d("locationviewmodel1", userId.toString())
        try {
            compositeDisposable.add(
                apiService.getLocationFavorites(userId)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            Log.d("locationviewmodel1", it.toString())
                            _downloadedJobResponse.postValue(it)
                            _networkState.postValue(NetworkState.LOADED)
                        },
                        {
                            _networkState.postValue(NetworkState.ERROR)
                            Log.d("locationviewmodel", it.message)
                        }
                    )
            )
        } catch (e: Exception) {
            Log.d("DataSource", e.message)
        }
    }
}