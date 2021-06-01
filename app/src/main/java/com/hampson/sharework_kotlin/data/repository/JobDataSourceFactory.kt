package com.hampson.sharework_kotlin.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.hampson.sharework_kotlin.data.api.JobDBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import io.reactivex.disposables.CompositeDisposable

class JobDataSourceFactory (private val apiService : JobDBInterface, private val compositeDisposable: CompositeDisposable) :
    DataSource.Factory<Int, Job>() {

    val jobLiveDataSource = MutableLiveData<JobDataSource>()

    override fun create(): DataSource<Int, Job> {
        val jobDataSource = JobDataSource(apiService, compositeDisposable)

        jobLiveDataSource.postValue(jobDataSource)
        return jobDataSource
    }

}