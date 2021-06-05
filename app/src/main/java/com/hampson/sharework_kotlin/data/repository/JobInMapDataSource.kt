package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.JobDBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class JobInMapDataSource (private val apiService : JobDBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedJobResponse = MutableLiveData<List<Job>>()
    val downlodedJobResponse: MutableLiveData<List<Job>>
        get() = _downloadedJobResponse

    fun fetchJobInMap(northeast_lat: Double, northeast_lng: Double, southwest_lat: Double, southwest_lng: Double) {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService.getOpenJobs(northeast_lat, northeast_lng, southwest_lat, southwest_lng)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _downloadedJobResponse.postValue(it.payload.jobList)
                            _networkState.postValue(NetworkState.LOADED)
                        },
                        {
                            _networkState.postValue(NetworkState.ERROR)
                            Log.d("JobDataSource", it.message)
                        }
                    )
            )
        } catch (e: Exception) {
            Log.d("JobDataSource", e.message)
        }
    }
}