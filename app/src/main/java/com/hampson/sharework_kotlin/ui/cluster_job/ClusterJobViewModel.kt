package com.hampson.sharework_kotlin.ui.cluster_job

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.ui.single_job.JobRepository
import io.reactivex.disposables.CompositeDisposable

class ClusterJobViewModel (private val jobRepository: JobPagedListRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val jobPagedList : LiveData<PagedList<Job>> by lazy {
        Log.d("TESTjobPagedList", "START")
        jobRepository.fetchLiveJobPagedList(compositeDisposable)
    }

    val networkState : LiveData<NetworkState> by lazy {
        jobRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        Log.d("TESTlistIsEmpty", "START")
        return jobPagedList.value?.isEmpty() ?: true
        Log.d("TESTlistIsEmpty", "START")
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}