package com.hampson.sharework_kotlin.ui.application_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.api.POST_PER_PAGE
import com.hampson.sharework_kotlin.data.repository.*
import com.hampson.sharework_kotlin.data.vo.*
import io.reactivex.disposables.CompositeDisposable

class HistoryRepository (private val apiService : DBInterface) {

    lateinit var applicationAppliedDataSource: ApplicationAppliedDataSource
    lateinit var applicationAppliedDataSourceFactory: ApplicationAppliedDataSourceFactory

    lateinit var applicationAppliedPagedList: LiveData<PagedList<JobApplication>>

    // applied
    fun getReviewPagedList (compositeDisposable: CompositeDisposable, status: String) : LiveData<PagedList<JobApplication>> {
        applicationAppliedDataSourceFactory = ApplicationAppliedDataSourceFactory(apiService, compositeDisposable, status)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        applicationAppliedPagedList = LivePagedListBuilder(applicationAppliedDataSourceFactory, config).build()

        return applicationAppliedPagedList
    }

    fun getAppliedPagedNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<ApplicationAppliedDataSource, NetworkState>(
            applicationAppliedDataSourceFactory.applicationLiveDataSource, ApplicationAppliedDataSource::networkState
        )
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return applicationAppliedDataSource.networkState
    }
}