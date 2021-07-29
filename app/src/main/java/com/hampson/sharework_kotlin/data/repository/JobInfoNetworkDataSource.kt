package com.hampson.sharework_kotlin.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.LocationFavorites
import com.hampson.sharework_kotlin.data.vo.Response
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class JobInfoNetworkDataSource (private val apiService : DBInterface, private val compositeDisposable : CompositeDisposable) {
    private val _networkState = MutableLiveData<NetworkState>()
    val networkState: LiveData<NetworkState>
        get() = _networkState

    private val _downloadedJobResponse = MutableLiveData<Response>()
    val downlodedJobResponse: MutableLiveData<Response>
        get() = _downloadedJobResponse

    private val _jobApplied = MutableLiveData<Boolean>()
    val jobApplied: MutableLiveData<Boolean>
        get() = _jobApplied

    fun getJobShow(jobId: Int) {
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
                        }
                    )
            )
        } catch (e: Exception) {

        }
    }

    fun createApplication(jobApplication: JobApplication) {
        try {
            compositeDisposable.add(
                apiService.createApplication(jobApplication)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            jobApplied.postValue(true)
                        },
                        {
                            jobApplied.postValue(false)
                        }
                    )
            )
        } catch (e: Exception) {

        }
    }
}