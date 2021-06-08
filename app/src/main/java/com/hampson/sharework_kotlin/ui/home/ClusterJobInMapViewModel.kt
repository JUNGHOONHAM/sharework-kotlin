package com.hampson.sharework_kotlin.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable

class ClusterJobInMapViewModel (private val jobRepository: JobInMapRepository, northeast_lat: Double, northeast_lng: Double, southwest_lat: Double, southwest_lng: Double) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val jobInMapList : MutableLiveData<List<Job>> by lazy {
        jobRepository.fetchSingleJobInMap(compositeDisposable, northeast_lat, northeast_lng, southwest_lat, southwest_lng)
    }

    val networkState : LiveData<NetworkState> by lazy {
        jobRepository.getJobNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return jobInMapList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun mapUpdate(northeast_lat: Double, northeast_lng: Double, southwest_lat: Double, southwest_lng: Double): MutableLiveData<List<Job>> {
        return jobRepository.fetchSingleJobInMap(compositeDisposable, northeast_lat, northeast_lng, southwest_lat, southwest_lng)
    }
}