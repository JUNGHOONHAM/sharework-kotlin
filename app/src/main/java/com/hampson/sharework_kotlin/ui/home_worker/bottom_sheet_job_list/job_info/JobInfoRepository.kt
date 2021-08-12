package com.hampson.sharework_kotlin.ui.home_worker.bottom_sheet_job_list.job_info

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.JobInfoNetworkDataSource
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Response
import io.reactivex.disposables.CompositeDisposable

class JobInfoRepository (private val apiService : DBInterface) {
    lateinit var jobInfoNetworkDataSource: JobInfoNetworkDataSource

    fun getJobShow (compositeDisposable: CompositeDisposable, jobId: Int) : MutableLiveData<Response> {
        jobInfoNetworkDataSource = JobInfoNetworkDataSource(apiService, compositeDisposable)
        jobInfoNetworkDataSource.getJobShow(jobId)

        return jobInfoNetworkDataSource.downlodedJobResponse
    }

    fun createApplication (compositeDisposable: CompositeDisposable, jobApplication: JobApplication) : MutableLiveData<Boolean> {
        jobInfoNetworkDataSource = JobInfoNetworkDataSource(apiService, compositeDisposable)
        jobInfoNetworkDataSource.createApplication(jobApplication)

        return jobInfoNetworkDataSource.jobApplied
    }

    fun getJobNetworkState(): LiveData<NetworkState> {
        return jobInfoNetworkDataSource.networkState
    }
}