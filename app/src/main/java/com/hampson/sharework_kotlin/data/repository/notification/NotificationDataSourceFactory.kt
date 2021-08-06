package com.hampson.sharework_kotlin.data.repository.notification

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Notification
import io.reactivex.disposables.CompositeDisposable

class NotificationDataSourceFactory (private val apiService: DBInterface, private val compositeDisposable: CompositeDisposable,
                                     private val userType: String) : DataSource.Factory<Int, Notification>() {

    val notificationLiveDataSource = MutableLiveData<NotificationDataSource>()

    override fun create(): DataSource<Int, Notification> {
        val notificationDataSource = NotificationDataSource(apiService, compositeDisposable, userType)

        notificationLiveDataSource.postValue(notificationDataSource)
        return notificationDataSource
    }

}