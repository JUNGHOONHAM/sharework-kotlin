package com.hampson.sharework_kotlin.data.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobApplication
import io.reactivex.disposables.CompositeDisposable

class ApplicationAppliedDataSourceFactory (private val apiService: DBInterface, private val compositeDisposable: CompositeDisposable,
                                           private val status: String) : DataSource.Factory<Int, JobApplication>() {

    val applicationLiveDataSource = MutableLiveData<ApplicationAppliedDataSource>()

    override fun create(): DataSource<Int, JobApplication> {
        val applicationDataSource = ApplicationAppliedDataSource(apiService, compositeDisposable, status)

        applicationLiveDataSource.postValue(applicationDataSource)
        return applicationDataSource
    }

}