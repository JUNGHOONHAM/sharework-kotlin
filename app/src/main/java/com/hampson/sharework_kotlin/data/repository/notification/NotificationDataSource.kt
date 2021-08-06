package com.hampson.sharework_kotlin.data.repository.notification

import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.hampson.sharework_kotlin.data.api.FIRST_PAGE
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Notification
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class NotificationDataSource (private val apiService : DBInterface, private val compositeDisposable: CompositeDisposable,
                              private val userType: String) : PageKeyedDataSource<Int, Notification>() {

    private var page = FIRST_PAGE

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Notification>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getNotifications(userType, params.key, 5)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        if (it.payload.meta.total_page >= params.key) {
                            callback.onResult(it.payload.notificationList, params.key + 1)
                            networkState.postValue(NetworkState.LOADED)
                        } else {
                            networkState.postValue(NetworkState.ENDOFLIST)
                        }
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Notification>) {

    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Notification>) {
        networkState.postValue(NetworkState.LOADING)
        compositeDisposable.add(
            apiService.getNotifications(userType, page, 5)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        callback.onResult(it.payload.notificationList, null, page + 1)
                        networkState.postValue(NetworkState.LOADED)
                    },
                    {
                        networkState.postValue(NetworkState.ERROR)
                    }
                )
        )
    }

}