package com.hampson.sharework_kotlin.ui.mypage.payment_history

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.api.POST_PER_PAGE
import com.hampson.sharework_kotlin.data.repository.*
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Meta
import io.reactivex.disposables.CompositeDisposable

class ApplicationPagedListRepository (private val apiService : DBInterface) {

    lateinit var ApplicationPagedList: LiveData<PagedList<JobApplication>>
    lateinit var applicationDataSourceFactory: ApplicationDataSourceFactory

    lateinit var paymentNetworkDataSource: PaymentNetworkDataSource

    fun fetchLiveApplicationPagedList (compositeDisposable: CompositeDisposable, userId: Int, startDate: String, endDate: String) : LiveData<PagedList<JobApplication>> {
        applicationDataSourceFactory = ApplicationDataSourceFactory(apiService, compositeDisposable, userId, startDate, endDate)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(POST_PER_PAGE)
            .build()

        ApplicationPagedList = LivePagedListBuilder(applicationDataSourceFactory, config).build()

        return ApplicationPagedList
    }

    fun fetchPaymentMeta (compositeDisposable: CompositeDisposable, startDate: String, endDate: String) : LiveData<Meta> {
        paymentNetworkDataSource = PaymentNetworkDataSource(apiService, compositeDisposable)
        paymentNetworkDataSource.fetchPaymentMeta(startDate, endDate)

        return paymentNetworkDataSource.downlodedJobResponse
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<ApplicationDataSource, NetworkState>(
            applicationDataSourceFactory.applicationLiveDataSource, ApplicationDataSource::networkState
        )
    }
}