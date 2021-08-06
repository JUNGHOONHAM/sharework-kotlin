package com.hampson.sharework_kotlin.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.api.POST_PER_PAGE
import com.hampson.sharework_kotlin.data.repository.JobDataSource
import com.hampson.sharework_kotlin.data.repository.JobDataSourceFactory
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.repository.notification.NotificationDataSource
import com.hampson.sharework_kotlin.data.repository.notification.NotificationDataSourceFactory
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.Notification
import io.reactivex.disposables.CompositeDisposable

class NotificationRepository (private val apiService : DBInterface) {

    lateinit var notificationPagedList: LiveData<PagedList<Notification>>
    lateinit var notificationDataSourceFactory: NotificationDataSourceFactory

    fun getNotifications (compositeDisposable: CompositeDisposable, userType: String) : LiveData<PagedList<Notification>> {
        notificationDataSourceFactory = NotificationDataSourceFactory(apiService, compositeDisposable, userType)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(POST_PER_PAGE)
            .build()

        notificationPagedList = LivePagedListBuilder(notificationDataSourceFactory, config).build()

        return notificationPagedList
    }

    fun getNetworkState(): LiveData<NetworkState> {
        return Transformations.switchMap<NotificationDataSource, NetworkState>(
            notificationDataSourceFactory.notificationLiveDataSource, NotificationDataSource::networkState
        )
    }
}