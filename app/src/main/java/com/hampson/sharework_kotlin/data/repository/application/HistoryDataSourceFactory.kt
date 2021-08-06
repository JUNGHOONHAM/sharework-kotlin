package com.hampson.sharework_kotlin.data.repository.application

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.JobApplication
import io.reactivex.disposables.CompositeDisposable

class HistoryDataSourceFactory (private val apiService: DBInterface, private val compositeDisposable: CompositeDisposable,
                                private val status: String) : DataSource.Factory<Int, JobApplication>() {

    val applicationLiveDataSource = MutableLiveData<HistoryDataSource>()

    override fun create(): DataSource<Int, JobApplication> {
        val applicationDataSource = HistoryDataSource(apiService, compositeDisposable, status)

        applicationLiveDataSource.postValue(applicationDataSource)
        return applicationDataSource
    }

}