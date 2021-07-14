package com.hampson.sharework_kotlin.ui.mypage.payment_history

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.api.POST_PER_PAGE
import com.hampson.sharework_kotlin.data.repository.ApplicationDataSource
import com.hampson.sharework_kotlin.data.repository.ApplicationDataSourceFactory
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.Job
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Meta
import com.hampson.sharework_kotlin.data.vo.User
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class PaymentHistoryWorkerViewModel (private val applicationRepository: ApplicationPagedListRepository,
                                     private val apiService: DBInterface, userId: Int, startDate: String, endDate: String) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val pageLiveData: MutableLiveData<PagedList<JobApplication>> = MutableLiveData()
    private val metaLiveData: MutableLiveData<Meta> = MutableLiveData()
    private val networkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()

    private val userId = userId
    private val startDate = startDate
    private val endDate = endDate

    fun getPage() : LiveData<PagedList<JobApplication>> {
        return pageLiveData
    }

    fun getMeta() : LiveData<Meta> {
        return metaLiveData
    }

    fun networkState() : LiveData<NetworkState> {
        return networkStateLiveData
    }

    val applicationPagedList : LiveData<PagedList<JobApplication>> by lazy {
        applicationRepository.fetchLiveApplicationPagedList(compositeDisposable)
    }

    val networkStatePagedList : LiveData<NetworkState> by lazy {
        applicationRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return applicationPagedList.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun test() {
        val applicationDataSourceFactory = ApplicationDataSourceFactory(apiService, compositeDisposable)

        val config = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setPageSize(POST_PER_PAGE)
            .build()

        val ApplicationPagedList = LivePagedListBuilder(applicationDataSourceFactory, config).build()

        pageLiveData.postValue(ApplicationPagedList.value)
    }

    fun getPaymentHistory() {
        networkStateLiveData.postValue(NetworkState.LOADING)

        try {
            compositeDisposable.add(
                apiService.getPaymentHistory(userId, startDate, endDate)
                    .subscribeOn(Schedulers.io())
                    .subscribe(
                        {
                            metaLiveData.postValue(it.payload.meta)
                            networkStateLiveData.postValue(NetworkState.LOADED)
                        },
                        {
                            networkStateLiveData.postValue(NetworkState.ERROR)
                        }
                    )
            )
        } catch (e: Exception) {
            networkStateLiveData.postValue(NetworkState.ERROR)
        }
    }
}