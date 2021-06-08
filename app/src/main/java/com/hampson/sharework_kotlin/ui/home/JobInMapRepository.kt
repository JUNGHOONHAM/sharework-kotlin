package com.hampson.sharework_kotlin.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.JobInMapDataSource
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable

class JobInMapRepository (private val apiService : DBInterface) {
    lateinit var jobInMapDataSource: JobInMapDataSource

    fun fetchSingleJobInMap (compositeDisposable: CompositeDisposable, northeast_lat: Double, northeast_lng: Double, southwest_lat: Double, southwest_lng: Double) : MutableLiveData<List<Job>> {
        jobInMapDataSource = JobInMapDataSource(apiService, compositeDisposable)
        jobInMapDataSource.fetchJobInMap(northeast_lat, northeast_lng, southwest_lat, southwest_lng)

        return jobInMapDataSource.downlodedJobResponse
    }

    fun getJobNetworkState(): LiveData<NetworkState> {
        return jobInMapDataSource.networkState
    }
}