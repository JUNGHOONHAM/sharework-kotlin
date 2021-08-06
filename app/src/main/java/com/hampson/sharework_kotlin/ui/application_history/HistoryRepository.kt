package com.hampson.sharework_kotlin.ui.application_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.api.POST_PER_PAGE
import com.hampson.sharework_kotlin.data.repository.*
import com.hampson.sharework_kotlin.data.repository.application.HistoryDataSource
import com.hampson.sharework_kotlin.data.repository.application.HistoryDataSourceFactory
import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.disposables.CompositeDisposable

class HistoryRepository (private val apiService : DBInterface) {

    lateinit var historyDataSource: HistoryDataSource
    lateinit var historyDataSourceFactory: HistoryDataSourceFactory

    lateinit var applicationPagedList: LiveData<PagedList<JobApplication>>

    // applied
    fun getReviewPagedList (compositeDisposable: CompositeDisposable, status: String) : LiveData<PagedList<JobApplication>> {
        historyDataSourceFactory = HistoryDataSourceFactory(apiService, compositeDisposable, status)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        applicationPagedList = LivePagedListBuilder(historyDataSourceFactory, config).build()

        return applicationPagedList
    }

    fun getAppliedPagedNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<HistoryDataSource, NetworkState>(
            historyDataSourceFactory.applicationLiveDataSource, HistoryDataSource::networkState
        )
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return historyDataSource.networkState
    }
}