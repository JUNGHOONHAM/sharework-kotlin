package com.hampson.sharework_kotlin.ui.mypage.payment_history

import androidx.lifecycle.*
import androidx.paging.PagedList
import com.hampson.sharework_kotlin.data.api.DBInterface
import com.hampson.sharework_kotlin.data.repository.NetworkState
import com.hampson.sharework_kotlin.data.vo.JobApplication
import com.hampson.sharework_kotlin.data.vo.Meta
import io.reactivex.disposables.CompositeDisposable


class PaymentHistoryWorkerViewModel (private val applicationRepository: ApplicationPagedListRepository) : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private val metaLiveData: MutableLiveData<Meta> = MutableLiveData()
    private val networkStateLiveData: MutableLiveData<NetworkState> = MutableLiveData()

    private val pageLiveData: MediatorLiveData<PagedList<JobApplication>> = MediatorLiveData<PagedList<JobApplication>>()
    private val paymentMetaLiveData: MediatorLiveData<Meta> = MediatorLiveData()

    fun getPageLiveData() : LiveData<PagedList<JobApplication>> {
        return pageLiveData
    }

    fun getMetaLiveData() : LiveData<Meta> {
        return paymentMetaLiveData
    }

    fun networkState() : LiveData<NetworkState> {
        return networkStateLiveData
    }

    val networkStatePagedList : LiveData<NetworkState> by lazy {
        applicationRepository.getNetworkState()
    }

    fun listIsEmpty(): Boolean {
        return pageLiveData.value?.isEmpty() ?: true
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getPaymentHistory(userId: Int, startDate: String, endDate: String) {
        val repositoryLiveData: LiveData<PagedList<JobApplication>> =
            applicationRepository.fetchLiveApplicationPagedList(compositeDisposable, userId, startDate, endDate)

        pageLiveData.addSource(repositoryLiveData) { value: PagedList<JobApplication> ->
            pageLiveData.setValue(value)
        }
    }

    fun getPaymentMeta(startDate: String, endDate: String) {
        val repositoryLiveData: LiveData<Meta> =
            applicationRepository.fetchPaymentMeta(compositeDisposable, startDate, endDate)

        paymentMetaLiveData.addSource(repositoryLiveData) { value: Meta ->
            paymentMetaLiveData.setValue(value)
        }
    }

    /**
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
    **/
}