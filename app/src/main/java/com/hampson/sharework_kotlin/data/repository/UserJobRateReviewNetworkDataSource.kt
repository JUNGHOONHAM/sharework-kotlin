package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class UserJobRateReviewNetworkDataSource (private val apiService : DBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedRateReviewResponse = MutableLiveData<UserJobRateReview>()
    val downloadedRateReviewResponse: MutableLiveData<UserJobRateReview>
        get() = _downloadedRateReviewResponse

    fun getRateReview(userId: Int, reviewCategory: String) {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService.getRateReview(userId, reviewCategory)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _downloadedRateReviewResponse.postValue(it.payload.userJobRateReview)
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