package com.hampson.sharework_kotlin.ui.single_job

import androidx.lifecycle.LiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.JobNetworkDataSource
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable

class JobRepository (private val apiService : DBInterface) {
    lateinit var jobNetworkDataSource: JobNetworkDataSource

    fun fetchSingleJob (compositeDisposable: CompositeDisposable, jobId: Int) : LiveData<Job> {
        jobNetworkDataSource = JobNetworkDataSource(apiService, compositeDisposable)
        jobNetworkDataSource.fetchJob(jobId)

        return jobNetworkDataSource.downlodedJobResponse
    }

    fun getJobNetworkState(): LiveData<NetworkState> {
        return jobNetworkDataSource.networkState
    }
}