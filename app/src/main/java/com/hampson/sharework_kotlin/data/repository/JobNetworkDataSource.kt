package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.JobDBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class JobNetworkDataSource (private val apiService : JobDBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedJobResponse = MutableLiveData<Job>()
    val downlodedJobResponse: LiveData<Job>
        get() = _downloadedJobResponse

    fun fetchJob(jobId: Int) {
        _networkState.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService.getJobShow(jobId)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            _downloadedJobResponse.postValue(it)
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