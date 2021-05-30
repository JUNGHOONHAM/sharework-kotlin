package com.hampson.sharework_kotlin.ui.single_job

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable

class SingleJobViewModel (private val jobRepository : JobRepository, jobId: Int) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val job : LiveData<Job> by lazy {
        jobRepository.fetchSingleJob(compositeDisposable, jobId)
    }

    val networkState : LiveData<NetworkState> by lazy {
        jobRepository.getJobNetworkState()
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}