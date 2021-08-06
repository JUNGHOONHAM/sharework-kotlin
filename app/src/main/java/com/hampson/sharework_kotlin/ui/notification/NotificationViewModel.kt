package com.hampson.sharework_kotlin.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.Notification
import io.reactivex.disposables.CompositeDisposable

class NotificationViewModel (private val notificationRepository: NotificationRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    val notificationPagedList : LiveData<PagedList<Notification>> by lazy {
        notificationRepository.getNotifications(compositeDisposable, "user")
    }

    val networkState : LiveData<NetworkState> by lazy {
        notificationRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return notificationPagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}